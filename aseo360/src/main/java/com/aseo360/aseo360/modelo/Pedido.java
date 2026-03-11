package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "pedidos")
public class Pedido{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "idCliente")
    private Cliente cliente;

    @Column(nullable = false)
    private BigDecimal totalPedido;

    @Column(nullable = false, length = 50)
    private LocalDate fecha;

    @Column(nullable = false, length = 30)
    private String formaPago;

    @Column(nullable = false, length = 20)
    private String estado;


}
