package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class InventarioRegistroDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El tipo es obligatorio (PRINCIPAL, TIENDA)")
    private String tipo;

    @NotNull(message = "El id de la sede es obligatorio")
    private Long idSede;
}
