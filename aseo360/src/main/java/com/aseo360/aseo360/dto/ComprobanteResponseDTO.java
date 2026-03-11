package com.aseo360.aseo360.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComprobanteResponseDTO {
    private Long id;
    private String serie;
    private Integer numero;
    private String tipoDocumento;
    private String estadoSunat;
    private String enlacePdfA4;
    private String enlacePdfTicket;
    private String mensaje;
}
