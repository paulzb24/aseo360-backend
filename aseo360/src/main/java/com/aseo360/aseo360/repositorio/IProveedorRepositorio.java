package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProveedorRepositorio extends JpaRepository<Proveedor, String> {
    boolean existsByNombre(String nombre);

    boolean existsByNombreAndRucNot(String nombre, String ruc);
}
