package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.ClienteTienda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IClienteTiendaRepositorio extends JpaRepository<ClienteTienda, Long> {
    Optional<ClienteTienda> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByCorreo(String correo);

    boolean existsByDniAndIdClienteTiendaNot(String dni, Long id);

    boolean existsByCorreoAndIdClienteTiendaNot(String correo, Long id);
}
