package com.aseo360.aseo360.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VentaResponseDTO {
    private Long idVenta;
    private String serieNumero;
    private String cliente;
    private BigDecimal total;
    private String fechaVenta;
    private String enlacePdfA4;
    private String enlacePdfTicket;
    private String mensaje;
}
