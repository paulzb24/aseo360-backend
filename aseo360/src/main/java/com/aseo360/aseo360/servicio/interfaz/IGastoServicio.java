package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.GastoRegistroDTO;
import com.aseo360.aseo360.modelo.Gasto;

import java.util.List;

public interface IGastoServicio {
    public List<Gasto> listarGastos();

    public Gasto registrarGasto(GastoRegistroDTO dto) throws Exception;

    public Gasto buscarGastoPorId(Long id) throws Exception;

    public Gasto modificarGasto(Long id, GastoRegistroDTO dto) throws Exception;

    public void eliminarGastoPorId(Long id);
}
