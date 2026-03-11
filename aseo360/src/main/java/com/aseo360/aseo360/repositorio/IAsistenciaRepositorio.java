package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Asistencia;
import com.aseo360.aseo360.modelo.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAsistenciaRepositorio extends JpaRepository<Asistencia, Long> {

        public interface EmpleadoPuntualProjection {
                String getNombre();

                Long getAsistenciasPuntuales();
        }

        public List<Asistencia> findByEmpleado(Empleado empleado);

        public Optional<Asistencia> findByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);

        public List<Asistencia> findByFecha(LocalDate fecha);

        @Query("SELECT e.nombreCompleto AS nombre, COUNT(a) AS asistenciasPuntuales " +
                        "FROM Asistencia a JOIN a.empleado e " +
                        "WHERE a.fecha BETWEEN :inicio AND :fin AND LOWER(a.estado) NOT LIKE '%falta%' " +
                        "GROUP BY e.nombreCompleto ORDER BY COUNT(a) DESC")
        List<EmpleadoPuntualProjection> obtenerTopEmpleadosPuntuales(@Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin, Pageable pageable);
}
