package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.Sede;
import com.aseo360.aseo360.modelo.TipoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInventarioRepositorio extends JpaRepository<Inventario, Long> {
    List<Inventario> findBySede(Sede sede);

    Optional<Inventario> findBySedeAndTipo(Sede sede, TipoInventario tipo);
}
