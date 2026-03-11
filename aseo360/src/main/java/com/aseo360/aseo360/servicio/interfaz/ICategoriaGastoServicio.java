package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.CategoriaGastoRegistroDTO;
import com.aseo360.aseo360.modelo.CategoriaGasto;

import java.util.List;

public interface ICategoriaGastoServicio {
    public List<CategoriaGasto> listarCategoriaGasto();

    public CategoriaGasto registrarCategoriaGasto(CategoriaGastoRegistroDTO dto) throws Exception;

    public CategoriaGasto modificarCategoriaGasto(Long id, CategoriaGastoRegistroDTO dto) throws Exception;

    public CategoriaGasto buscarCategoriaGastoPorId(Long id) throws Exception;

    public void eliminarCategoriaGasto(Long id);
}
