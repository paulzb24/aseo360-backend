package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.ProductoCarritoDTO;
import com.aseo360.aseo360.dto.VentaRegistroDTO;
import com.aseo360.aseo360.dto.VentaResponseDTO;
import com.aseo360.aseo360.modelo.*;
import com.aseo360.aseo360.repositorio.*;
import com.aseo360.aseo360.servicio.interfaz.IInventarioServicio;
import com.aseo360.aseo360.servicio.interfaz.IProductoServicio;
import com.aseo360.aseo360.servicio.interfaz.IVentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class VentaServicio implements IVentaServicio {
    private final IVentaRepositorio ventaRepositorio;
    private final IDetalleVentaRepositorio detalleVentaRepositorio;
    private final IClienteTiendaRepositorio clienteTiendaRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final IProductoServicio productoServicio;
    private final IComprobanteRepositorio comprobanteRepositorio;
    private final SunatService sunatService;
    private final IInventarioServicio inventarioServicio;
    private final IInventarioRepositorio inventarioRepositorio;

    @Autowired
    public VentaServicio(IVentaRepositorio ventaRepositorio, IDetalleVentaRepositorio detalleVentaRepositorio,
            IClienteTiendaRepositorio clienteTiendaRepositorio, IProductoRepositorio productoRepositorio,
            IProductoServicio productoServicio, IComprobanteRepositorio comprobanteRepositorio,
            SunatService sunatService, IInventarioServicio inventarioServicio,
            IInventarioRepositorio inventarioRepositorio) {
        this.detalleVentaRepositorio = detalleVentaRepositorio;
        this.ventaRepositorio = ventaRepositorio;
        this.clienteTiendaRepositorio = clienteTiendaRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.productoServicio = productoServicio;
        this.comprobanteRepositorio = comprobanteRepositorio;
        this.sunatService = sunatService;
        this.inventarioServicio = inventarioServicio;
        this.inventarioRepositorio = inventarioRepositorio;
    }

    @Override
    public Page<Venta> listarVentas(Pageable pageable) {
        return this.ventaRepositorio.findAll(pageable);
    }

    @Override
    public List<DetalleVenta> listarDetallesPorVentaId(Long id) throws Exception {
        Optional<Venta> venta = this.ventaRepositorio.findById(id);
        if (venta.isEmpty()) {
            throw new Exception("No se pudo encontrar la venta");
        }
        Optional<List<DetalleVenta>> detalleVentas = this.detalleVentaRepositorio.findAllByVenta(venta.get());
        if (detalleVentas.isEmpty()) {
            throw new Exception("No hay detalles de esta venta");
        }
        return detalleVentas.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VentaResponseDTO registrarVenta(VentaRegistroDTO ventaRegistroDTO) throws Exception {
        Venta venta = new Venta();
        LocalDate hoy = LocalDate.now();

        ClienteTienda clienteTienda = clienteTiendaRepositorio.findById(ventaRegistroDTO.getIdClienteTienda())
                .orElseThrow(() -> new Exception("No se encontró el cliente"));

        Inventario inventario = inventarioRepositorio.findById(ventaRegistroDTO.getIdInventario())
                .orElseThrow(() -> new Exception("Inventario no encontrado"));

        venta.setClienteTienda(clienteTienda);
        venta.setInventario(inventario);
        venta.setFormaPago(ventaRegistroDTO.getFormaPago());
        venta.setTipoDocumento(ventaRegistroDTO.getTipoDocumento());
        venta.setEstado(ventaRegistroDTO.getEstado());

        if ("FACTURA".equalsIgnoreCase(venta.getTipoDocumento()) && clienteTienda.getDni().length() != 11) {
            throw new Exception(
                    "Inconsistencia tributaria: No se puede emitir una FACTURA si el cliente no posee un RUC de 11 dígitos (Documento actual: "
                            + clienteTienda.getDni() + ").");
        }

        venta.setFechaVenta(hoy);
        venta.setTotalVenta(BigDecimal.ZERO);

        // Guardamos inicialmente para generar el ID (correlativo para SUNAT)
        Venta ventaGuardada = this.ventaRepositorio.save(venta);

        BigDecimal ventaTotalAcumulado = BigDecimal.ZERO;
        List<DetalleVenta> detallesGuardados = new ArrayList<>();

        for (ProductoCarritoDTO item : ventaRegistroDTO.getProductoList()) {
            Producto producto = this.productoRepositorio.findById(item.getIdProducto())
                    .orElseThrow(() -> new Exception("Producto no encontrado: " + item.getIdProducto()));

            // Descontar stock usando el nuevo servicio
            inventarioServicio.actualizarStock(
                    inventario.getIdInventario(),
                    producto.getIdProducto(),
                    item.getCantidad(),
                    "VENTA",
                    "Venta #" + ventaGuardada.getIdVenta(),
                    "Sistema");

            BigDecimal precioAplicado = calcularPrecio(item.getCantidad(), producto);
            BigDecimal subTotalLinea = precioAplicado.multiply(new BigDecimal(item.getCantidad()));

            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(ventaGuardada);
            detalleVenta.setCantidad(item.getCantidad());
            detalleVenta.setPrecioUnitario(precioAplicado);
            detalleVenta.setProducto(producto);
            detalleVenta.setSubTotal(subTotalLinea);

            // Guardamos y añadimos a una lista para el servicio de SUNAT
            detallesGuardados.add(this.detalleVentaRepositorio.save(detalleVenta));

            ventaTotalAcumulado = ventaTotalAcumulado.add(subTotalLinea);
        }

        ventaGuardada.setTotalVenta(ventaTotalAcumulado);
        Venta ventaFinal = this.ventaRepositorio.save(ventaGuardada);
        String pdfUrlA4 = null;
        String pdfUrlTicket = null;

        // --- LÓGICA DE COMPROBANTE ELECTRÓNICO ---

        if ("COMPLETADO".equalsIgnoreCase(ventaFinal.getEstado())
                || "PAGADO".equalsIgnoreCase(ventaFinal.getEstado())) {

            boolean esTicket = "TICKET".equalsIgnoreCase(ventaFinal.getTipoDocumento());

            // Creamos el registro en la nueva tabla de Comprobantes
            Comprobante comp = new Comprobante();
            comp.setVenta(ventaFinal);

            // Datos del documento
            boolean esFactura = "FACTURA".equalsIgnoreCase(ventaFinal.getTipoDocumento());
            comp.setSerie(esTicket ? "T001" : (esFactura ? "F001" : "B001"));
            comp.setNumero(ventaFinal.getIdVenta().intValue());
            comp.setTipoDocumento(ventaFinal.getTipoDocumento().toUpperCase());
            comp.setTotalComprobante(ventaFinal.getTotalVenta());

            // Datos del cliente
            comp.setClienteNombre(clienteTienda.getNombreCompleto());
            comp.setClienteDocumento(clienteTienda.getDni());
            comp.setFechaEmision(LocalDateTime.now());

            if (!esTicket) {
                try {
                    // Llamamos al servicio que ya devuelve el objeto SunatResponse
                    SunatResponse res = sunatService.enviarComprobante(ventaFinal);

                    if (res != null && res.isSuccess()) {
                        // Datos de SUNAT
                        comp.setEstadoSunat(res.getPayload().getEstado());
                        comp.setHash(res.getPayload().getHash());
                        comp.setEnlacePdfA4(res.getPayload().getPdf().getA4());
                        comp.setEnlacePdfTicket(res.getPayload().getPdf().getTicket());
                        comp.setEnlaceXml(res.getPayload().getXml());
                        comp.setEnlaceCdr(res.getPayload().getCdr());
                        comp.setMensajeSunat(res.getMessage());

                        this.comprobanteRepositorio.save(comp);

                        pdfUrlA4 = res.getPayload().getPdf().getA4();
                        pdfUrlTicket = res.getPayload().getPdf().getTicket();
                        System.out.println("Comprobante legal guardado correctamente.");
                    } else {
                        System.err.println(
                                "SUNAT rechazó el envío: " + (res != null ? res.getMessage() : "Error desconocido"));
                    }
                } catch (Exception e) {
                    // Logueamos pero no frenamos la transacción principal si falla la red
                    System.err.println("Error de red con el servicio de SUNAT: " + e.getMessage());
                }
            } else {
                // Registrar solo de forma interna sin envío a SUNAT
                comp.setEstadoSunat("INTERNO");
                this.comprobanteRepositorio.save(comp);
                System.out.println("Ticket interno guardado correctamente.");
            }
        }

        return VentaResponseDTO.builder()
                .idVenta(ventaFinal.getIdVenta())
                .cliente(ventaFinal.getClienteTienda().getNombreCompleto())
                .total(ventaFinal.getTotalVenta())
                .fechaVenta(ventaFinal.getFechaVenta().toString())
                .enlacePdfA4(pdfUrlA4)
                .enlacePdfTicket(pdfUrlTicket)
                .serieNumero("TICKET".equalsIgnoreCase(ventaFinal.getTipoDocumento())
                        ? "T001-" + ventaFinal.getIdVenta()
                        : ventaFinal.getTipoDocumento().substring(0, 1).toUpperCase() + "001-"
                                + ventaFinal.getIdVenta())
                .mensaje("Venta registrada correctamente")
                .build();
    }

    @Override
    public Venta buscarVentaPorId(Long id) throws Exception {
        return this.ventaRepositorio.findById(id).orElseThrow(() -> new Exception("Error: venta no encontrada."));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Venta anularVenta(Long id) throws Exception {
        Venta venta = this.ventaRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Venta no encontrada con id: " + id));
        if ("ANULADO".equalsIgnoreCase(venta.getEstado())) {
            throw new Exception("Error: la venta ya se encuentra anulada.");
        }
        venta.setEstado("ANULADO");
        List<DetalleVenta> detalles = this.detalleVentaRepositorio.findAllByVenta(venta)
                .orElseThrow(() -> new Exception("Venta no encontrada"));
        for (DetalleVenta detalle : detalles) {
            // Devolver stock al inventario original
            inventarioServicio.actualizarStock(
                    venta.getInventario().getIdInventario(),
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad(),
                    "DEVOLUCION", // O VENTA_ANULADA
                    "Anulación Venta #" + venta.getIdVenta(),
                    "Sistema");
        }

        // --- ANULACIÓN EN SUNAT ---
        try {
            boolean esFactura = "factura".equalsIgnoreCase(venta.getTipoDocumento());
            String serie = esFactura ? "F001" : "B001";
            String numero = String.valueOf(venta.getIdVenta());

            SunatResponse res;
            if (esFactura) {
                res = sunatService.anularFactura(serie, numero);
            } else {
                res = sunatService.anularBoleta(serie, numero);
            }

            // Actualizar el comprobante existente
            Optional<Comprobante> compOpt = this.comprobanteRepositorio.findByVenta(venta);
            if (compOpt.isPresent()) {
                Comprobante comp = compOpt.get();
                comp.setEstadoSunat("ANULADO");
                if (res != null && res.getMessage() != null) {
                    comp.setMensajeSunat("ANULADO - " + res.getMessage());
                }
                this.comprobanteRepositorio.save(comp);
            }

            if (res != null && res.isSuccess()) {
                System.out.println("Comprobante anulado correctamente en SUNAT.");
            } else {
                System.err.println("Error al anular en SUNAT: " + (res != null ? res.getMessage() : "Sin respuesta"));
            }
        } catch (Exception e) {
            System.err.println("Error de red al anular en SUNAT: " + e.getMessage());
        }

        return this.ventaRepositorio.save(venta);
    }

    private BigDecimal calcularPrecio(Integer cantidad, Producto producto) {
        return (cantidad > 3) ? producto.getPrecioPorMayor() : producto.getPrecioVenta();
    }

}
