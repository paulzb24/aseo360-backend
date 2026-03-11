package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.DetalleAdquisicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDetalleAdquisicionRepositorio extends JpaRepository<DetalleAdquisicion, Long> {
    List<DetalleAdquisicion> findByAdquisicionIdAdquisicion(Long idAdquisicion);

    void deleteByAdquisicionIdAdquisicion(Long idAdquisicion);
}
