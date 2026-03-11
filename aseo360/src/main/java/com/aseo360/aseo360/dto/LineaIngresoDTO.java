package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class LineaIngresoDTO {
    @NotBlank(message = "El producto es obligatorio")
    private String idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El costo unitario es obligatorio")
    private BigDecimal precioCosto;

    @NotBlank(message = "El proveedor es obligatorio para el ingreso")
    private String idProveedor;
}
