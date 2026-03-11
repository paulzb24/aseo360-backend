package com.aseo360.aseo360.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PedidoRegistroDTO {
    private Long idCliente;

    @NotBlank(message = "La forma de pago es obligatoria")
    private String formaPago;

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<ProductoCarritoDTO> productoList;
}
