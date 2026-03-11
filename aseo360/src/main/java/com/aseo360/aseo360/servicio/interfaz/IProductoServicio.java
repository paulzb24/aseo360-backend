package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.ProductoRegistroDTO;
import com.aseo360.aseo360.dto.ProductoResponseDTO;
import com.aseo360.aseo360.modelo.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductoServicio {
    public Page<ProductoResponseDTO> listarProductos(Pageable pageable) throws Exception;

    public Page<ProductoResponseDTO> listarProductosDisponibles(Pageable pageable) throws Exception;

    public Producto registrarProducto(ProductoRegistroDTO productoRegistroDTO) throws Exception;

    public Producto modificarProducto(ProductoRegistroDTO productoRegistroDTO) throws Exception;

    public Producto buscarPorId(String id) throws Exception;

    public void eliminarPorId(String id) throws Exception;

    public Producto aumentarStock(String id, Integer cantidad) throws Exception;
}
