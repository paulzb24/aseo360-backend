package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.AdquisicionEstadoDTO;
import com.aseo360.aseo360.dto.AdquisicionRegistroDTO;
import com.aseo360.aseo360.modelo.Adquisicion;

import java.util.List;

public interface IAdquisicionServicio {
    List<Adquisicion> listarAdquisiciones();

    Adquisicion buscarPorId(Long id);

    Adquisicion registrarAdquisicion(AdquisicionRegistroDTO dto);

    Adquisicion modificarAdquisicion(Long id, AdquisicionRegistroDTO dto);

    Adquisicion cambiarEstado(Long id, AdquisicionEstadoDTO dto);

    void eliminarAdquisicion(Long id);
}
