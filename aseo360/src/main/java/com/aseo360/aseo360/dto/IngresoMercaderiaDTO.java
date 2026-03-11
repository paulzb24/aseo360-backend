package com.aseo360.aseo360.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class IngresoMercaderiaDTO {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La sede es obligatoria")
    private Long idSede;

    @NotNull(message = "El tipo de ingreso es obligatorio")
    private String tipoIngreso;

    private String observacion;

    @NotEmpty(message = "Debe haber al menos un producto a ingresar")
    @Valid
    private List<LineaIngresoDTO> lineas;
}
