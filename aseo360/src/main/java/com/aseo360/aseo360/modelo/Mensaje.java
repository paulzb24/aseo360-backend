package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "mensajes")
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "idCliente")
    private Cliente cliente;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false)
    private String cuerpo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 40)
    private String estado;

}
