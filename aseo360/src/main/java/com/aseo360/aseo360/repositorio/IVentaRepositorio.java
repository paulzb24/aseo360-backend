package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IVentaRepositorio extends JpaRepository<Venta, Long> {

    public interface MetodoPagoProjection {
        String getMetodo();

        BigDecimal getTotal();
    }

    public interface ClienteTiendaProjection {
        String getNombre();

        Long getCantidadVentas();

        BigDecimal getTotalComprado();
    }

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.estado != 'ANULADO'")
    BigDecimal obtenerTotalVentasRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT v.formaPago AS metodo, SUM(v.totalVenta) AS total " +
            "FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.estado != 'ANULADO' " +
            "GROUP BY v.formaPago")
    List<MetodoPagoProjection> obtenerVentasPorMetodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT c.nombreCompleto AS nombre, COUNT(v) AS cantidadVentas, SUM(v.totalVenta) AS totalComprado " +
            "FROM Venta v JOIN v.clienteTienda c " +
            "WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.estado != 'ANULADO' " +
            "GROUP BY c.nombreCompleto ORDER BY COUNT(v) DESC")
    List<ClienteTiendaProjection> obtenerTopClientes(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin,
            Pageable pageable);
}
