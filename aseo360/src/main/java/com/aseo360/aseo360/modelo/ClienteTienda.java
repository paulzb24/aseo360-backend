package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "clientes_tienda")
public class ClienteTienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClienteTienda;

    @Column(length = 150, nullable = false)
    private String nombreCompleto;

    @Column(length = 30, nullable = false, unique = true)
    private String dni;

    @Column(unique = true)
    private String correo;

    private String direccion;
}
