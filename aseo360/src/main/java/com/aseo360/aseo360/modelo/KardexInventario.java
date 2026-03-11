package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "kardex_inventario")
public class KardexInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKardex;

    @ManyToOne
    @JoinColumn(name = "inventario_id", referencedColumnName = "idInventario", nullable = false)
    private Inventario inventario;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "idProducto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "proveedor_ruc", referencedColumnName = "ruc")
    private Proveedor proveedor;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String tipoMovimiento; // ENTRADA, SALIDA, TRASLADO_ENTRADA, TRASLADO_SALIDA, VENTA

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer stockAnterior;

    @Column(nullable = false)
    private Integer stockActual;

    private String referencia; // ID Venta, ID Traslado, etc.
}
