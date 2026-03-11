package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "adquisiciones")
public class Adquisicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAdquisicion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 100)
    private String solicitadoPor;

    @Column(nullable = false, length = 20)
    private String prioridad; // BAJA, MEDIA, ALTA, URGENTE

    @Column(nullable = false, length = 100)
    private String area;

    @Column(nullable = false, length = 20)
    private String estado; // EN_PROCESO, COMPLETADO, CANCELADO

    private BigDecimal totalEstimado;

    @OneToMany(mappedBy = "adquisicion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAdquisicion> detalles;
}
