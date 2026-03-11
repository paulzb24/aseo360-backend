package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "productos")
public class Producto {
    @Id
    private String idProducto;

    @ManyToOne
    @JoinColumn(name = "categoria_id", referencedColumnName = "idCategoria")
    private CategoriaProducto categoriaProducto;

    @ManyToOne
    @JoinColumn(name = "aroma_id", referencedColumnName = "idAroma")
    private Aroma aroma;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private String imagen;

    @Column(nullable = false)
    private BigDecimal precioCompra;

    @Column(nullable = false)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private BigDecimal precioPorMayor;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private String estado;

    private String peso;
    private String presentacion;
}
