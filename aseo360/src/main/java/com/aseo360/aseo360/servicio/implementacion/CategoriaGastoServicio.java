package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.CategoriaGastoRegistroDTO;
import com.aseo360.aseo360.modelo.CategoriaGasto;
import com.aseo360.aseo360.repositorio.ICategoriaGastoRepositorio;
import com.aseo360.aseo360.servicio.interfaz.ICategoriaGastoServicio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaGastoServicio implements ICategoriaGastoServicio {

    private final ICategoriaGastoRepositorio categoriaGastoRepositorio;

    public CategoriaGastoServicio(ICategoriaGastoRepositorio categoriaGastoRepositorio) {
        this.categoriaGastoRepositorio = categoriaGastoRepositorio;
    }

    @Override
    public List<CategoriaGasto> listarCategoriaGasto() {
        return this.categoriaGastoRepositorio.findAll();
    }

    @Override
    public CategoriaGasto registrarCategoriaGasto(CategoriaGastoRegistroDTO dto) throws Exception {
        if (this.categoriaGastoRepositorio.existsByNombre(dto.getNombre())) {
            throw new Exception("Ya existe una categoria de gasto con el nombre: " + dto.getNombre());
        }
        CategoriaGasto categoriaGasto = new CategoriaGasto();
        categoriaGasto.setNombre(dto.getNombre());
        categoriaGasto.setDescripcion(dto.getDescripcion());
        return this.categoriaGastoRepositorio.save(categoriaGasto);
    }

    @Override
    public CategoriaGasto modificarCategoriaGasto(Long id, CategoriaGastoRegistroDTO dto) throws Exception {
        CategoriaGasto categoriaGasto = this.categoriaGastoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Error: categoria no encontrada"));

        if (this.categoriaGastoRepositorio.existsByNombreAndIdCategoriaGastoNot(dto.getNombre(), id)) {
            throw new Exception("Ya existe otra categoria de gasto con el nombre: " + dto.getNombre());
        }
        categoriaGasto.setNombre(dto.getNombre());
        categoriaGasto.setDescripcion(dto.getDescripcion());
        return this.categoriaGastoRepositorio.save(categoriaGasto);
    }

    @Override
    public CategoriaGasto buscarCategoriaGastoPorId(Long id) throws Exception {
        return this.categoriaGastoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Error: categoria no encontrado"));
    }

    @Override
    public void eliminarCategoriaGasto(Long id) {
        this.categoriaGastoRepositorio.deleteById(id);
    }
}
