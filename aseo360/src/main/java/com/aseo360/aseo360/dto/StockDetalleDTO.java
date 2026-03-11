package com.aseo360.aseo360.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDetalleDTO {
    private Long idInventario;
    private String nombreInventario;
    private Long idSede;
    private String nombreSede;
    private Integer cantidad;
}
