package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.ComprobanteManualDTO;
import com.aseo360.aseo360.dto.ComprobanteResponseDTO;
import com.aseo360.aseo360.dto.GuiaRemisionDTO;
import com.aseo360.aseo360.modelo.Comprobante;
import com.aseo360.aseo360.modelo.SunatResponse;
import com.aseo360.aseo360.repositorio.IComprobanteRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IComprobanteServicio;
import com.aseo360.aseo360.servicio.interfaz.IVentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

@Service
public class ComprobanteServicio implements IComprobanteServicio {

    private static final ZoneId ZONA_PERU = ZoneId.of("America/Lima");

    private final SunatService sunatService;
    private final IComprobanteRepositorio comprobanteRepositorio;
    private final IVentaServicio ventaServicio;

    @Autowired
    public ComprobanteServicio(SunatService sunatService, IComprobanteRepositorio comprobanteRepositorio,
            @Lazy IVentaServicio ventaServicio) {
        this.sunatService = sunatService;
        this.comprobanteRepositorio = comprobanteRepositorio;
        this.ventaServicio = ventaServicio;
    }

    @Override
    public Page<Comprobante> listarComprobantes(Pageable pageable) {
        return comprobanteRepositorio.findAllByOrderByFechaEmisionDesc(pageable);
    }

    @Override
    public ComprobanteResponseDTO emitirComprobanteManual(ComprobanteManualDTO dto) throws Exception {
        // 1. Validar fecha de emisión (hora Perú)
        validarFechaEmision(dto.getFechaEmision());

        // 2. Validar documento del cliente según tipo de comprobante
        validarDocumentoCliente(dto);

        // 3. Calcular total general
        BigDecimal totalGeneral = calcularTotal(dto);

        // 4. Enviar a SUNAT
        SunatResponse res = sunatService.enviarComprobanteManual(dto);

        // 5. Procesar respuesta de SUNAT
        String pdfUrlA4 = null;
        String pdfUrlTicket = null;
        String estadoSunat = "PENDIENTE";
        String hash = null;
        String mensajeSunat = "Comprobante registrado localmente.";

        if (res != null && res.isSuccess()) {
            estadoSunat = res.getPayload() != null ? res.getPayload().getEstado() : "ACEPTADO";
            hash = res.getPayload() != null ? res.getPayload().getHash() : null;
            pdfUrlA4 = res.getPayload() != null && res.getPayload().getPdf() != null
                    ? res.getPayload().getPdf().getA4()
                    : null;
            pdfUrlTicket = res.getPayload() != null && res.getPayload().getPdf() != null
                    ? res.getPayload().getPdf().getTicket()
                    : null;
            mensajeSunat = res.getMessage() != null ? res.getMessage() : "Comprobante emitido correctamente.";
        } else if (res != null) {
            mensajeSunat = "SUNAT rechazó: " + res.getMessage();
            estadoSunat = "RECHAZADO";
        }

        // 6. Guardar en la tabla comprobantes
        Comprobante comp = new Comprobante();
        comp.setVenta(null);
        comp.setSerie(dto.getSerie());
        comp.setNumero(dto.getNumero());
        comp.setTipoDocumento(dto.getTipoDocumento().toUpperCase());
        comp.setTotalComprobante(totalGeneral.setScale(2, RoundingMode.HALF_UP));
        comp.setClienteNombre(dto.getClienteDenominacion());
        comp.setClienteDocumento(dto.getClienteNumeroDoc());
        comp.setEstadoSunat(estadoSunat);
        comp.setHash(hash);
        comp.setEnlacePdfA4(pdfUrlA4);
        comp.setEnlacePdfTicket(pdfUrlTicket);
        comp.setMensajeSunat(mensajeSunat);
        comp.setFechaEmision(LocalDateTime.now(ZONA_PERU));

        if (res != null && res.isSuccess() && res.getPayload() != null) {
            comp.setEnlaceXml(res.getPayload().getXml());
            comp.setEnlaceCdr(res.getPayload().getCdr());
        }

        Comprobante saved = comprobanteRepositorio.save(comp);

        // 7. Construir y retornar respuesta
        return ComprobanteResponseDTO.builder()
                .id(saved.getId())
                .serie(dto.getSerie())
                .numero(dto.getNumero())
                .tipoDocumento(dto.getTipoDocumento().toUpperCase())
                .estadoSunat(estadoSunat)
                .enlacePdfA4(pdfUrlA4)
                .enlacePdfTicket(pdfUrlTicket)
                .mensaje(mensajeSunat)
                .build();
    }

    // --- Métodos privados de validación y cálculo ---

    @Override
    public ComprobanteResponseDTO anularComprobante(Long id) throws Exception {
        Comprobante comp = comprobanteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado con id: " + id));

        if ("ANULADO".equalsIgnoreCase(comp.getEstadoSunat())) {
            throw new IllegalArgumentException("Este comprobante ya fue anulado.");
        }

        // Si el comprobante proviene de una venta, anular la venta (devuelve stock,
        // etc)
        if (comp.getVenta() != null) {
            // Nota: VentaServicio.anularVenta ya se encarga de llamar a SUNAT y de
            // marcar el comprobante como anulado.
            this.ventaServicio.anularVenta(comp.getVenta().getIdVenta());

            // Recargamos el comprobante para devolver la data actualizada
            comp = comprobanteRepositorio.findById(id).get();

            return ComprobanteResponseDTO.builder()
                    .id(comp.getId())
                    .serie(comp.getSerie())
                    .numero(comp.getNumero())
                    .tipoDocumento(comp.getTipoDocumento())
                    .estadoSunat(comp.getEstadoSunat())
                    .mensaje(comp.getMensajeSunat())
                    .build();
        }

        // Si es un comprobante manual (venta == null), se sigue el flujo original:
        // Llamar a SUNAT para anular
        boolean esFactura = "FACTURA".equalsIgnoreCase(comp.getTipoDocumento());
        String serie = comp.getSerie();
        String numero = String.valueOf(comp.getNumero());

        SunatResponse res;
        if (esFactura) {
            res = sunatService.anularFactura(serie, numero);
        } else {
            res = sunatService.anularBoleta(serie, numero);
        }

        // Actualizar estado en BD
        comp.setEstadoSunat("ANULADO");
        String mensajeAnulacion = "Comprobante anulado.";
        if (res != null && res.getMessage() != null) {
            mensajeAnulacion = "ANULADO - " + res.getMessage();
        }
        comp.setMensajeSunat(mensajeAnulacion);
        comprobanteRepositorio.save(comp);

        return ComprobanteResponseDTO.builder()
                .id(comp.getId())
                .serie(comp.getSerie())
                .numero(comp.getNumero())
                .tipoDocumento(comp.getTipoDocumento())
                .estadoSunat("ANULADO")
                .mensaje(mensajeAnulacion)
                .build();
    }

    @Override
    public ComprobanteResponseDTO consultarEstado(Long id) throws Exception {
        Comprobante comp = comprobanteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado con id: " + id));

        // TICKET no se envía a SUNAT
        if ("TICKET".equalsIgnoreCase(comp.getTipoDocumento())) {
            throw new IllegalArgumentException("Los TICKETs son internos y no se consultan en SUNAT.");
        }

        SunatResponse res = sunatService.consultarEstado(
                comp.getTipoDocumento(),
                comp.getSerie(),
                comp.getNumero());

        if (res != null) {
            if (res.isSuccess() && res.getPayload() != null) {
                comp.setEstadoSunat(res.getPayload().getEstado());
                comp.setMensajeSunat(res.getMessage() != null ? res.getMessage() : "Consulta exitosa");

                // Actualizar links si vienen en la respuesta (por si acaso)
                if (res.getPayload().getPdf() != null) {
                    if (res.getPayload().getPdf().getA4() != null)
                        comp.setEnlacePdfA4(res.getPayload().getPdf().getA4());
                    if (res.getPayload().getPdf().getTicket() != null)
                        comp.setEnlacePdfTicket(res.getPayload().getPdf().getTicket());
                }
                if (res.getPayload().getXml() != null)
                    comp.setEnlaceXml(res.getPayload().getXml());
                if (res.getPayload().getCdr() != null)
                    comp.setEnlaceCdr(res.getPayload().getCdr());
            } else {
                comp.setMensajeSunat("Error SUNAT: " + res.getMessage());
            }
            comprobanteRepositorio.save(comp);
        } else {
            throw new Exception("Sin respuesta del servicio SUNAT");
        }

        return ComprobanteResponseDTO.builder()
                .id(comp.getId())
                .serie(comp.getSerie())
                .numero(comp.getNumero())
                .tipoDocumento(comp.getTipoDocumento())
                .estadoSunat(comp.getEstadoSunat())
                .mensaje(comp.getMensajeSunat())
                .build();
    }

    @Override
    public Map<String, Object> emitirGuiaRemision(GuiaRemisionDTO dto) throws Exception {
        return sunatService.enviarGuiaRemision(dto);
    }

    private void validarFechaEmision(String fechaStr) throws Exception {
        try {
            LocalDate fechaEmision = LocalDate.parse(fechaStr);
            LocalDate hoy = LocalDate.now(ZONA_PERU);
            LocalDate limiteAnterior = hoy.minusDays(4);

            if (fechaEmision.isAfter(hoy)) {
                throw new IllegalArgumentException("La fecha de emisión no puede ser una fecha futura.");
            }
            if (fechaEmision.isBefore(limiteAnterior)) {
                throw new IllegalArgumentException(
                        "La fecha de emisión no puede ser mayor a 4 días antes de hoy (" + limiteAnterior + ").");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha de emisión tiene un formato inválido. Use el formato yyyy-MM-dd.");
        }
    }

    private void validarDocumentoCliente(ComprobanteManualDTO dto) throws Exception {
        if ("FACTURA".equalsIgnoreCase(dto.getTipoDocumento()) &&
                (dto.getClienteNumeroDoc() == null || dto.getClienteNumeroDoc().length() != 11)) {
            throw new IllegalArgumentException(
                    "Para emitir una FACTURA, el cliente debe tener un RUC válido de 11 dígitos.");
        }

        if ("BOLETA".equalsIgnoreCase(dto.getTipoDocumento()) &&
                dto.getClienteNumeroDoc() != null && !dto.getClienteNumeroDoc().isEmpty() &&
                dto.getClienteNumeroDoc().length() != 8 && dto.getClienteNumeroDoc().length() != 11) {
            throw new IllegalArgumentException(
                    "El documento del cliente debe ser un DNI (8 dígitos) o RUC (11 dígitos).");
        }
    }

    private BigDecimal calcularTotal(ComprobanteManualDTO dto) {
        BigDecimal totalGeneral = BigDecimal.ZERO;
        for (var item : dto.getItems()) {
            BigDecimal subtotal = item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
            totalGeneral = totalGeneral.add(subtotal);
        }
        return totalGeneral;
    }
}
