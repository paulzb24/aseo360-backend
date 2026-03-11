package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.DetalleVenta;
import com.aseo360.aseo360.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface IDetalleVentaRepositorio extends JpaRepository<DetalleVenta, Long> {

        public interface ProductoProjection {
                String getNombre();

                Long getCantidadVendida();

                BigDecimal getIngresos();
        }

        public Optional<List<DetalleVenta>> findAllByVenta(Venta venta);

        @Query("SELECT p.nombre AS nombre, SUM(d.cantidad) AS cantidadVendida, SUM(d.SubTotal) AS ingresos " +
                        "FROM DetalleVenta d JOIN d.producto p JOIN d.venta v " +
                        "WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.estado != 'ANULADO' " +
                        "GROUP BY p.nombre ORDER BY SUM(d.cantidad) DESC")
        List<ProductoProjection> obtenerTopProductos(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin,
                        Pageable pageable);
}
