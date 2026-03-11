package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEmpleadoRepositorio extends JpaRepository<Empleado, Long> {
    public Optional<Empleado> findByCorreo(String correo);

    // Para cuando modificas: busca si existe ese DNI EN OTRO usuario (excluyendo tu
    // ID)
    boolean existsByDniAndIdEmpleadoNot(String dni, Long id);

    boolean existsByNumeroCelularAndIdEmpleadoNot(String numeroTelefono, Long id);

    boolean existsByDni(String dni);

    boolean existsByNumeroCelular(String numeroCelular);
}
