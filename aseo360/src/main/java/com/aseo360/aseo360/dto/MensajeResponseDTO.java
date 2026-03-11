package com.aseo360.aseo360.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponseDTO {
    private Long idMensaje;
    private String nombreRemitente;
    private String correoRemitente;
    private String dni;
    private String numeroCelular;
    private String direccion;
    private String asuntoMensaje;
    private String cuerpoMensaje;
    private LocalDate fechaMensaje;
    private String estado;
}
