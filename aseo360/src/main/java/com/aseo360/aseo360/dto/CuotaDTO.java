package com.aseo360.aseo360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CuotaDTO {
    @NotNull(message = "El importe de la cuota es obligatorio")
    private BigDecimal importe;

    @NotBlank(message = "La fecha de pago de la cuota es obligatoria")
    private String fechaDePago; // yyyy-MM-dd
}
