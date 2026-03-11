package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.GuiaRemisionDTO;
import com.aseo360.aseo360.modelo.*;
import com.aseo360.aseo360.repositorio.IDetalleVentaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class SunatService {

    @Value("${sunat.api.url}")
    private String URL;

    @Value("${sunat.api.voided.url}")
    private String urlAnulacionFactura;

    @Value("${sunat.api.daily-summary.url}")
    private String urlAnulacionBoleta;

    @Value("${sunat.api.status.url:https://app.apisunat.pe/api/v3/status}")
    private String urlStatus;

    @Value("${sunat.api.dispatches.url:https://app.apisunat.pe/api/v3/dispatches}")
    private String urlGuiaRemision;

    @Value("${sunat.api.token}")
    private String TOKEN;

    @Autowired
    private IDetalleVentaRepositorio detalleVentaRepositorio;

    // Metodo auxiliar para crear los headers comunes
    private HttpHeaders crearHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(TOKEN);
        return headers;
    }

    // Enviar comprobante (factura o boleta) a SUNAT
    public SunatResponse enviarComprobante(Venta venta) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = crearHeaders();

        Map<String, Object> body = new HashMap<>();
        ClienteTienda cliente = venta.getClienteTienda();
        List<DetalleVenta> detalles = this.detalleVentaRepositorio.findAllByVenta(venta)
                .orElse(Collections.emptyList());

        boolean esFactura = cliente.getDni().length() == 11 && "factura".equalsIgnoreCase(venta.getTipoDocumento());

        body.put("documento", esFactura ? "factura" : "boleta");
        body.put("serie", esFactura ? "F001" : "B001");
        body.put("numero", venta.getIdVenta().intValue());
        body.put("fecha_de_emision", LocalDate.now().toString());
        body.put("hora_de_emision", LocalTime.now().withNano(0).toString());
        body.put("moneda", "PEN");
        body.put("tipo_operacion", "0101");
        body.put("cliente_tipo_de_documento", esFactura ? "6" : "1");
        body.put("cliente_numero_de_documento", cliente.getDni());
        body.put("cliente_denominacion", cliente.getNombreCompleto().toUpperCase());
        body.put("cliente_direccion", cliente.getDireccion() != null ? cliente.getDireccion() : "LIMA");

        List<Map<String, String>> itemsList = detalles.stream().map(d -> {
            Map<String, String> item = new HashMap<>();
            item.put("unidad_de_medida", "NIU");
            item.put("descripcion", d.getProducto().getNombre());
            item.put("cantidad", String.valueOf(d.getCantidad()));

            BigDecimal valorUnitario = d.getPrecioUnitario().divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
            item.put("valor_unitario", valorUnitario.toString());

            item.put("porcentaje_igv", "18");
            item.put("codigo_tipo_afectacion_igv", "10");
            item.put("nombre_tributo", "IGV");
            return item;
        }).toList();
        body.put("items", itemsList);

        body.put("total", venta.getTotalVenta().setScale(2, RoundingMode.HALF_UP).toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonTask = mapper.writeValueAsString(body);

            System.out.println("ENVIANDO ESTE JSON EXACTO: " + jsonTask);

            HttpEntity<String> entity = new HttpEntity<>(jsonTask, headers);
            return restTemplate.postForObject(URL, entity, SunatResponse.class);

        } catch (Exception e) {
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            errorRes.setMessage("Error en el envío: " + e.getMessage());
            return errorRes;
        }
    }

    // Enviar comprobante manual (datos directos del frontend, sin
    // Venta/DetalleVenta)
    public SunatResponse enviarComprobanteManual(com.aseo360.aseo360.dto.ComprobanteManualDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = crearHeaders();

        Map<String, Object> body = new LinkedHashMap<>();
        boolean esFactura = "FACTURA".equalsIgnoreCase(dto.getTipoDocumento());
        boolean esCredito = "CREDITO".equalsIgnoreCase(dto.getCondicionPago());

        // --- 1. Datos del documento ---
        body.put("documento", esFactura ? "factura" : "boleta");
        body.put("serie", dto.getSerie());
        body.put("numero", dto.getNumero());
        body.put("fecha_de_emision", dto.getFechaEmision());
        body.put("hora_de_emision", LocalTime.now().withNano(0).toString());
        body.put("moneda",
                "PEN".equalsIgnoreCase(dto.getMoneda()) || "SOLES".equalsIgnoreCase(dto.getMoneda()) ? "PEN" : "USD");
        body.put("tipo_operacion", "0101");

        // --- 2. Fecha de vencimiento (solo crédito) ---
        if (esCredito && dto.getFechaVencimiento() != null && !dto.getFechaVencimiento().isEmpty()) {
            body.put("fecha_de_vencimiento", dto.getFechaVencimiento());
        }

        // --- 3. Datos del cliente ---
        body.put("cliente_tipo_de_documento", dto.getClienteTipoDoc());
        body.put("cliente_numero_de_documento", dto.getClienteNumeroDoc());
        body.put("cliente_denominacion", dto.getClienteDenominacion().toUpperCase());
        body.put("cliente_direccion", dto.getClienteDireccion() != null ? dto.getClienteDireccion() : "LIMA");

        // --- 4. Cuotas (solo crédito) ---
        if (esCredito && dto.getCuotas() != null && !dto.getCuotas().isEmpty()) {
            List<Map<String, String>> cuotasList = new ArrayList<>();
            for (com.aseo360.aseo360.dto.CuotaDTO cuota : dto.getCuotas()) {
                Map<String, String> cuotaMap = new LinkedHashMap<>();
                cuotaMap.put("importe", cuota.getImporte().setScale(2, RoundingMode.HALF_UP).toString());
                cuotaMap.put("fecha_de_pago", cuota.getFechaDePago());
                cuotasList.add(cuotaMap);
            }
            body.put("cuotas", cuotasList);
        }

        // --- 5. Ítems del comprobante ---
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<Map<String, String>> itemsList = new ArrayList<>();

        for (com.aseo360.aseo360.dto.ItemComprobanteDTO item : dto.getItems()) {
            Map<String, String> itemMap = new LinkedHashMap<>();
            itemMap.put("unidad_de_medida", item.getUnidadMedida() != null ? item.getUnidadMedida() : "NIU");
            itemMap.put("descripcion", item.getDescripcion());
            itemMap.put("cantidad", String.valueOf(item.getCantidad()));

            // Calcular valor unitario sin IGV (alta precisión para evitar diferencias con
            // SUNAT)
            BigDecimal valorUnitario = item.getPrecioUnitario().divide(new BigDecimal("1.18"), 10,
                    RoundingMode.HALF_UP);
            itemMap.put("valor_unitario", valorUnitario.toString());

            itemMap.put("porcentaje_igv", "18");
            itemMap.put("codigo_tipo_afectacion_igv", "10");
            itemMap.put("nombre_tributo", "IGV");

            itemsList.add(itemMap);

            // Acumular total desde precio original (con IGV)
            BigDecimal subtotalLinea = item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
            totalGeneral = totalGeneral.add(subtotalLinea);
        }

        body.put("items", itemsList);

        // --- 6. Total ---
        body.put("total", totalGeneral.setScale(2, RoundingMode.HALF_UP).toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonTask = mapper.writeValueAsString(body);

            System.out.println("ENVIANDO COMPROBANTE MANUAL: " + jsonTask);

            HttpEntity<String> entity = new HttpEntity<>(jsonTask, headers);
            return restTemplate.postForObject(URL, entity, SunatResponse.class);

        } catch (Exception e) {
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            errorRes.setMessage("Error en el envío manual: " + e.getMessage());
            return errorRes;
        }
    }

    // Anular factura: Comunicación de Baja (POST /api/v3/voided)
    public SunatResponse anularFactura(String serie, String numero) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = crearHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("documento", "comunicacion_baja");
        body.put("motivo", "ANULACIÓN DE OPERACIÓN");

        Map<String, String> documentoAfectado = new HashMap<>();
        documentoAfectado.put("documento", "factura");
        documentoAfectado.put("serie", serie);
        documentoAfectado.put("numero", numero);
        body.put("documento_afectado", documentoAfectado);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);

            System.out.println("ANULANDO FACTURA - JSON: " + jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            return restTemplate.postForObject(urlAnulacionFactura, entity, SunatResponse.class);

        } catch (Exception e) {
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            errorRes.setMessage("Error al anular factura: " + e.getMessage());
            return errorRes;
        }
    }

    // Anular boleta: Resumen Diario (POST /api/v3/daily-summary)
    public SunatResponse anularBoleta(String serie, String numero) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = crearHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("documento", "resumen_diario");

        Map<String, String> docAfectado = new HashMap<>();
        docAfectado.put("accion_resumen", "anular");
        docAfectado.put("documento", "boleta");
        docAfectado.put("serie", serie);
        docAfectado.put("numero", numero);

        List<Map<String, String>> documentosAfectados = new ArrayList<>();
        documentosAfectados.add(docAfectado);
        body.put("documentos_afectados", documentosAfectados);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);

            System.out.println("ANULANDO BOLETA - JSON: " + jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            return restTemplate.postForObject(urlAnulacionBoleta, entity, SunatResponse.class);

        } catch (Exception e) {
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            errorRes.setMessage("Error al anular boleta: " + e.getMessage());
            return errorRes;
        }
    }

    // Consultar estado de comprobante (POST /api/v3/status)
    public SunatResponse consultarEstado(String tipoDocumento, String serie, Integer numero) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = crearHeaders();

        Map<String, Object> body = new HashMap<>();
        boolean esFactura = "FACTURA".equalsIgnoreCase(tipoDocumento);
        body.put("documento", esFactura ? "factura" : "boleta");
        body.put("serie", serie);
        body.put("numero", numero);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);

            System.out.println("CONSULTANDO ESTADO - JSON: " + jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            return restTemplate.postForObject(urlStatus, entity, SunatResponse.class);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("Error 4xx en consulta estado: " + e.getResponseBodyAsString());
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            /* Handle SUNAT JSON errors dynamically */
            try {
                ObjectMapper mapper = new ObjectMapper();
                SunatResponse parsed = mapper.readValue(e.getResponseBodyAsString(), SunatResponse.class);
                if (parsed.getMessage() != null) {
                    errorRes.setMessage(parsed.getMessage());
                } else {
                    errorRes.setMessage("Documento no encontrado o no válido");
                }
            } catch (Exception parseEx) {
                errorRes.setMessage("Error al consultar estado (4xx): " + e.getMessage());
            }
            return errorRes;
        } catch (Exception e) {
            SunatResponse errorRes = new SunatResponse();
            errorRes.setSuccess(false);
            errorRes.setMessage("Error al consultar estado: " + e.getMessage());
            return errorRes;
        }
    }

    // Enviar Guía de Remisión (POST /api/v3/dispatches)
    public Map<String, Object> enviarGuiaRemision(GuiaRemisionDTO guiaDTO) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convertir el DTO directamente a JSON ya que coincide exactamente con la
            // estructura de SUNAT
            String jsonBody = mapper.writeValueAsString(guiaDTO);
            System.out.println("====== PAYLOAD ENVIADO A SUNAT (GUIA) ======");
            System.out.println(jsonBody);
            System.out.println("============================================");

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, crearHeaders());
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                    urlGuiaRemision,
                    org.springframework.http.HttpMethod.POST,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Parsear respuesta exitosa
                return mapper.readValue(response.getBody(), Map.class);
            } else {
                throw new Exception(
                        "Error al emitir guía de remisión: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String cuerpoError = e.getResponseBodyAsString();
            System.out.println("====== ERROR RESPUESTA SUNAT ======");
            System.out.println(cuerpoError);
            System.out.println("===================================");
            throw new Exception("Error en la emisión de guía: " + e.getStatusCode() + " - " + cuerpoError);
        } catch (Exception e) {
            throw new Exception("Error interno en la emisión de la guía de remisión: " + e.getMessage());
        }
    }
}
