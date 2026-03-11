package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.KardexInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IKardexInventarioRepositorio extends JpaRepository<KardexInventario, Long> {
    Page<KardexInventario> findByInventario(Inventario inventario, Pageable pageable);

    java.util.List<KardexInventario> findByProducto(com.aseo360.aseo360.modelo.Producto producto);
}
