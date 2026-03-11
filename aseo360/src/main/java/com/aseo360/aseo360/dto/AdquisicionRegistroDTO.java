package com.aseo360.aseo360.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdquisicionRegistroDTO {

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "El campo 'solicitado por' es obligatorio")
    private String solicitadoPor;

    @NotBlank(message = "La prioridad es obligatoria")
    private String prioridad;

    @NotBlank(message = "El área es obligatoria")
    private String area;

    @NotNull(message = "Debe incluir al menos un item")
    @Valid
    private List<DetalleAdquisicionDTO> items;
}
