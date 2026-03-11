package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemGuiaDTO {
    private String codigo_interno;
    private String descripcion;
    private String unidad_de_medida; // "NIU", "KGM", etc.
    private Integer cantidad;
}
