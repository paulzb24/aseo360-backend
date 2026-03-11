package com.aseo360.aseo360.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VentaRegistroDTO {
    @NotNull(message = "El cliente de tienda es obligatorio")
    private Long idClienteTienda;

    @NotNull(message = "Debe seleccionar un inventario")
    private Long idInventario;

    @NotBlank(message = "La forma de pago es obligatoria")
    private String formaPago;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    private String estado;

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<ProductoCarritoDTO> productoList;
}
