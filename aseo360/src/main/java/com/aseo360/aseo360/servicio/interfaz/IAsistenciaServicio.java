package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.AsistenciaDTO;
import com.aseo360.aseo360.modelo.Asistencia;

import java.time.LocalDate;
import java.util.List;

public interface IAsistenciaServicio {
    public List<AsistenciaDTO> listarAsistencias() throws Exception;

    public List<Asistencia> listarAsistenciasPorFecha(LocalDate fecha) throws Exception;

    public List<AsistenciaDTO> listarAsistenciasPorEmpleado(String correo) throws Exception;

    public Asistencia registrarAsistencia(String correo) throws Exception;

    public Asistencia registrarAsistenciaAdmin(AsistenciaDTO dto) throws Exception;

    public Asistencia registrarSalida(Long idAsistencia) throws Exception;

    public Asistencia modificarAsistencia(AsistenciaDTO modificarAsistencia) throws Exception;

    public Asistencia modificarComentario(AsistenciaDTO modificarComentario) throws Exception;

    public Asistencia modificarEstado(AsistenciaDTO modificarEstado) throws Exception;
}
