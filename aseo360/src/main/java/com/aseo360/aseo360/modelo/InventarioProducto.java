package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "inventario_productos", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "inventario_id", "producto_id" })
})
public class InventarioProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventario_id", referencedColumnName = "idInventario", nullable = false)
    private Inventario inventario;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "idProducto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer stock;
}
