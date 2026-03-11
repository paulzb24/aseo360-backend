package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Adquisicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAdquisicionRepositorio extends JpaRepository<Adquisicion, Long> {
    List<Adquisicion> findAllByOrderByFechaHoraDesc();
}
