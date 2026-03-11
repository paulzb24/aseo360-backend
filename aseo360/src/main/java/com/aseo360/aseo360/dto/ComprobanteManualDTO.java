package com.aseo360.aseo360.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ComprobanteManualDTO {
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento; // "FACTURA" o "BOLETA"

    @NotBlank(message = "La serie es obligatoria")
    private String serie; // F001, B001

    @NotNull(message = "El número correlativo es obligatorio")
    private Integer numero;

    @NotBlank(message = "La fecha de emisión es obligatoria")
    private String fechaEmision; // yyyy-MM-dd

    @NotBlank(message = "La moneda es obligatoria")
    private String moneda; // PEN o USD

    // "1" = DNI, "6" = RUC
    @NotBlank(message = "El tipo de documento del cliente es obligatorio")
    private String clienteTipoDoc;

    @NotBlank(message = "El número de documento del cliente es obligatorio")
    private String clienteNumeroDoc;

    @NotBlank(message = "El nombre/razón social del cliente es obligatorio")
    private String clienteDenominacion;

    private String clienteDireccion;

    private String condicionPago; // CONTADO / CREDITO

    private String fechaVencimiento; // Solo si es CREDITO (yyyy-MM-dd)

    @Valid
    private List<CuotaDTO> cuotas; // Solo si es CREDITO

    @NotEmpty(message = "Debe incluir al menos un ítem")
    @Valid
    private List<ItemComprobanteDTO> items;
}
