package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductoRegistroDTO {

    @NotBlank(message = "El código del producto es obligatorio")
    private String idProducto;

    @NotNull(message = "La categoría del producto es obligatoria")
    private Long idCategoriaProducto;

    private Long idAroma;

    private Long idInventario;

    // Se usa SOLAMENTE para el ingreso inicial en el kardex, NO se guarda en
    // Producto
    private String idProveedor;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private String descripcion;
    private String imagen;

    private Integer cantidad;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @DecimalMin(value = "0.0", message = "El precio por mayor no puede ser negativo")
    private BigDecimal precioMayor;
    private String peso;
    private String presentacion;

    @NotBlank(message = "El estado del producto es obligatorio")
    private String estado;

}
