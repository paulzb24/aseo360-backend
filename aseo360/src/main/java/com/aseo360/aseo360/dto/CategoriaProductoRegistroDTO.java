package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CategoriaProductoRegistroDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
}
