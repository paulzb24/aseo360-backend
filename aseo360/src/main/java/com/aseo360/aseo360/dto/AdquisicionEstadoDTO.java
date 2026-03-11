package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdquisicionEstadoDTO {
    @NotBlank(message = "El estado es obligatorio")
    private String estado; // EN_PROCESO, COMPLETADO, CANCELADO
}
