package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "traslados_inventario")
public class TrasladoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraslado;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "idProducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "inventario_origen_id", referencedColumnName = "idInventario", nullable = false)
    private Inventario inventarioOrigen;

    @ManyToOne
    @JoinColumn(name = "inventario_destino_id", referencedColumnName = "idInventario", nullable = false)
    private Inventario inventarioDestino;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private String usuario;

    private String motivo;

    @Column(nullable = false, length = 20)
    private String estado = "EN_PROCESO"; // EN_PROCESO, FINALIZADO, CANCELADO
}
