package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IGastoRepositorio extends JpaRepository<Gasto, Long> {

        public interface TipoGastoProjection {
                String getTipoGasto();

                Long getCantidadGastos();

                BigDecimal getTotalGastado();
        }

        @Query("SELECT g.tipoGasto AS tipoGasto, COUNT(g) AS cantidadGastos, SUM(g.monto) AS totalGastado " +
                        "FROM Gasto g " +
                        "WHERE g.fecha BETWEEN :inicio AND :fin " +
                        "GROUP BY g.tipoGasto ORDER BY SUM(g.monto) DESC")
        List<TipoGastoProjection> obtenerTopTiposGasto(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin,
                        Pageable pageable);
}
