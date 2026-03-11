package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.modelo.ClienteTienda;

import java.util.List;

public interface IClienteTiendaServicio {
    public List<ClienteTienda> listarClienteTienda();

    public ClienteTienda registrarClienteTienda(ClienteTienda clienteTienda) throws Exception;

    public ClienteTienda buscarPorId(Long idCliente) throws Exception;

    public ClienteTienda buscarPorDNI(String dni) throws Exception;

    public ClienteTienda modificarClienteTienda(ClienteTienda clienteTienda) throws Exception;

    public void eliminarClienteTiendaPorId(Long id);
}
