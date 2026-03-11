package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.CategoriaProductoRegistroDTO;
import com.aseo360.aseo360.modelo.CategoriaProducto;
import com.aseo360.aseo360.repositorio.ICategoriaProductoRepositorio;
import com.aseo360.aseo360.servicio.interfaz.ICategoriaProductoServicio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaProductoServicio implements ICategoriaProductoServicio {

    private final ICategoriaProductoRepositorio categoriaProductoRepositorio;

    public CategoriaProductoServicio(ICategoriaProductoRepositorio categoriaProductoRepositorio) {
        this.categoriaProductoRepositorio = categoriaProductoRepositorio;
    }

    @Override
    public List<CategoriaProducto> listarCategoriaProducto() {
        return this.categoriaProductoRepositorio.findAll();
    }

    @Override
    public CategoriaProducto registrarCatProducto(CategoriaProductoRegistroDTO dto) throws Exception {
        if (this.categoriaProductoRepositorio.existsByNombre(dto.getNombre())) {
            throw new Exception("Ya existe una categoria con el nombre: " + dto.getNombre());
        }
        CategoriaProducto categoriaProducto = new CategoriaProducto();
        categoriaProducto.setNombre(dto.getNombre());
        categoriaProducto.setDescripcion(dto.getDescripcion());
        return this.categoriaProductoRepositorio.save(categoriaProducto);
    }

    @Override
    public CategoriaProducto buscarCatProductoPorId(Long id) throws Exception {
        return this.categoriaProductoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Error: categoria no encontrado"));
    }

    @Override
    public CategoriaProducto modificarCatProducto(Long id, CategoriaProductoRegistroDTO dto) throws Exception {
        CategoriaProducto categoriaProducto = this.categoriaProductoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Error: categoria no encontrada"));

        if (this.categoriaProductoRepositorio.existsByNombreAndIdCategoriaNot(dto.getNombre(), id)) {
            throw new Exception("Ya existe otra categoria con el nombre: " + dto.getNombre());
        }
        categoriaProducto.setNombre(dto.getNombre());
        categoriaProducto.setDescripcion(dto.getDescripcion());
        return this.categoriaProductoRepositorio.save(categoriaProducto);
    }

    @Override
    public void eliminarCatProductoPorId(Long id) {
        this.categoriaProductoRepositorio.deleteById(id);
    }
}
