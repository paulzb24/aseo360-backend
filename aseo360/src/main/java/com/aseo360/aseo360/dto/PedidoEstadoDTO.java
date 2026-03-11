package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoEstadoDTO {
    @NotNull(message = "El id del pedido es obligatorio")
    private Long idPedido;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
