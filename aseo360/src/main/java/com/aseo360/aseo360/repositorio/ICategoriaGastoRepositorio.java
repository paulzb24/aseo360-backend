package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.CategoriaGasto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaGastoRepositorio extends JpaRepository<CategoriaGasto, Long> {
    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdCategoriaGastoNot(String nombre, Long id);
}
