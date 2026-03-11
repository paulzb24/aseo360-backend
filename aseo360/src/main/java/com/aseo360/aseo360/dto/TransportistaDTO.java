package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransportistaDTO {
    private String ruc;
    private String denominacion;
    private String numero_registro_MTC;
    private String numero_autorizacion;
    private String codigo_entidad_autorizadora;
}
