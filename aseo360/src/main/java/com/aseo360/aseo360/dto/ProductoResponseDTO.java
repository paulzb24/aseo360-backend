package com.aseo360.aseo360.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private String idProducto;
    private String imagen;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal precioMayor;
    private Long idCategoriaProducto;
    private String categoria;
    private Long idAroma;
    private String aroma;
    private String peso;
    private String presentacion;
    private LocalDate fechaRegistro;
    private String estado;
    private List<StockDetalleDTO> stock;
}
