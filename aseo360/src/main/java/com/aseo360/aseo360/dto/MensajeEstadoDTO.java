package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MensajeEstadoDTO {
    @NotNull(message = "El id del mensaje es obligatorio")
    private Long idMensaje;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
