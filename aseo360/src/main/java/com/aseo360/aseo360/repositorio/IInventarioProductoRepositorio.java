package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.InventarioProducto;
import com.aseo360.aseo360.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInventarioProductoRepositorio extends JpaRepository<InventarioProducto, Long> {
    Optional<InventarioProducto> findByInventarioAndProducto(Inventario inventario, Producto producto);

    List<InventarioProducto> findByInventario(Inventario inventario);

    List<InventarioProducto> findByProducto(Producto producto);

    List<InventarioProducto> findByProductoIn(List<Producto> productos);
}
