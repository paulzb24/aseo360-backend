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
public class SalidaMercaderiaDTO {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La sede de origen es obligatoria")
    private Long idSede;

    @NotNull(message = "El motivo de la salida es obligatorio")
    private String motivo;

    private String observacion;

    @NotEmpty(message = "Debe haber al menos un producto a retirar")
    @Valid
    private List<LineaSalidaDTO> lineas;
}
