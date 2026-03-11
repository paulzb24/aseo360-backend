package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class TrasladoInventarioDTO {
    @NotNull(message = "El id de origen es obligatorio")
    private Long idOrigen;

    @NotNull(message = "El id de destino es obligatorio")
    private Long idDestino;

    @NotBlank(message = "El id del producto es obligatorio")
    private String idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    private String motivo;

    private String estado;
}
