package com.aseo360.aseo360.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comprobantes")
public class Comprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venta_id", referencedColumnName = "idVenta", nullable = true)
    @JsonIgnoreProperties({ "clienteTienda", "inventario", "hibernateLazyInitializer", "handler" })
    private Venta venta;

    // Campos para comprobantes manuales (cuando no hay venta asociada)
    private String serie;
    private Integer numero;
    private String tipoDocumento;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal totalComprobante;

    private String clienteNombre;
    private String clienteDocumento;

    private String estadoSunat; // ACEPTADO, RECHAZADO, PENDIENTE
    private String hash; // Código hash de la firma digital

    @Column(length = 500)
    private String enlacePdfA4; // El link A4 que ya probamos

    @Column(length = 500)
    private String enlacePdfTicket; // El link Ticket que ya probamos

    @Column(length = 500)
    private String enlaceXml; // El archivo XML legal

    @Column(length = 500)
    private String enlaceCdr; // Constancia de Recepción de SUNAT

    @Column(columnDefinition = "TEXT")
    private String mensajeSunat;
    private LocalDateTime fechaEmision;
}
