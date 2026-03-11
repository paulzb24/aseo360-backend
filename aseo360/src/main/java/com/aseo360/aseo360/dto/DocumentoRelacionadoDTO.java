package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentoRelacionadoDTO {
    private String documento; // ej. "factura"
    private String serie; // ej. "F001"
    private String numero; // ej. "3108"
    private String ruc_emisor; // opcional, ej "20553300429"
}
