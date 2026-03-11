package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConductorDTO {
    private String conductor; // "principal" u otro
    private String tipo_de_documento; // "1" DNI, "6" RUC
    private String numero_de_documento;
    private String nombres;
    private String apellidos;
    private String numero_licencia_conducir;
}
