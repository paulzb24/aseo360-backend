package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "idClienteTienda")
    private ClienteTienda clienteTienda;

    @ManyToOne
    @JoinColumn(name = "inventario_id", referencedColumnName = "idInventario")
    private Inventario inventario;

    @Column(nullable = false)
    private LocalDate fechaVenta;

    @Column(nullable = false)
    private BigDecimal totalVenta;

    @Column(nullable = false)
    private String formaPago;

    @Column(nullable = false)
    private String tipoDocumento;

    @Column(nullable = false)
    private String estado;
}
