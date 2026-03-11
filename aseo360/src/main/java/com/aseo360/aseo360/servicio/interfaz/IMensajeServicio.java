package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.MensajeEstadoDTO;
import com.aseo360.aseo360.dto.MensajeRegistroDTO;
import com.aseo360.aseo360.dto.MensajeResponseDTO;
import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.modelo.Mensaje;

import java.util.List;

public interface IMensajeServicio{
    public List<MensajeResponseDTO> listarMensajes();
    public Mensaje registrarMensaje(Cliente cliente, MensajeRegistroDTO mensajeRegistroDTO);
    public Mensaje modificarEstado(MensajeEstadoDTO mensajeEstadoDTO) throws Exception;
    public void eliminarMensaje(Long id);
}
