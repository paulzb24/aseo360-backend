package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMensajeRepositorio extends JpaRepository<Mensaje, Long> {
}
