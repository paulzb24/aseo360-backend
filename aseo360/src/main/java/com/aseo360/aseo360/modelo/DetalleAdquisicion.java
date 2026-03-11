package com.aseo360.aseo360.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "detalle_adquisiciones")
public class DetalleAdquisicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleAdquisicion;

    @ManyToOne
    @JoinColumn(name = "adquisicion_id", referencedColumnName = "idAdquisicion")
    @JsonIgnore
    private Adquisicion adquisicion;

    @Column(nullable = false, length = 200)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal precioTotal;
}
