package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.modelo.ClienteTienda;
import com.aseo360.aseo360.repositorio.IClienteTiendaRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IClienteTiendaServicio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteTiendaServicio implements IClienteTiendaServicio {

    private final IClienteTiendaRepositorio clienteTiendaRepositorio;

    public ClienteTiendaServicio(IClienteTiendaRepositorio clienteTiendaRepositorio) {
        this.clienteTiendaRepositorio = clienteTiendaRepositorio;
    }

    @Override
    public List<ClienteTienda> listarClienteTienda() {
        return this.clienteTiendaRepositorio.findAll();
    }

    @Override
    public ClienteTienda registrarClienteTienda(ClienteTienda clienteTienda) throws Exception {
        if (clienteTienda.getDni() == null
                || (clienteTienda.getDni().length() != 8 && clienteTienda.getDni().length() != 11)) {
            throw new Exception("El número de documento (DNI/RUC) debe tener exactamente 8 o 11 dígitos");
        }

        if (this.clienteTiendaRepositorio.existsByDni(clienteTienda.getDni())) {
            throw new Exception("Ya existe un cliente con el DNI/RUC: " + clienteTienda.getDni());
        }
        if (clienteTienda.getCorreo() != null
                && this.clienteTiendaRepositorio.existsByCorreo(clienteTienda.getCorreo())) {
            throw new Exception("Ya existe un cliente con el correo: " + clienteTienda.getCorreo());
        }
        return this.clienteTiendaRepositorio.save(clienteTienda);
    }

    @Override
    public ClienteTienda buscarPorId(Long idCliente) throws Exception {
        return this.clienteTiendaRepositorio.findById(idCliente)
                .orElseThrow(() -> new Exception("Error: cliente no encontrado"));
    }

    @Override
    public ClienteTienda buscarPorDNI(String dni) throws Exception {
        return this.clienteTiendaRepositorio.findByDni(dni)
                .orElseThrow(() -> new Exception("No se encontró un usuario con dni " + dni));
    }

    @Override
    public ClienteTienda modificarClienteTienda(ClienteTienda clienteTienda) throws Exception {
        if (clienteTienda.getIdClienteTienda() == null || clienteTienda.getIdClienteTienda() <= 0) {
            throw new Exception("Error: el id es necesario para actualizar cliente");
        }

        if (clienteTienda.getDni() == null
                || (clienteTienda.getDni().length() != 8 && clienteTienda.getDni().length() != 11)) {
            throw new Exception("El número de documento (DNI/RUC) debe tener exactamente 8 o 11 dígitos");
        }

        if (this.clienteTiendaRepositorio.existsByDniAndIdClienteTiendaNot(clienteTienda.getDni(),
                clienteTienda.getIdClienteTienda())) {
            throw new Exception("Ya existe otro cliente con el DNI/RUC: " + clienteTienda.getDni());
        }
        if (clienteTienda.getCorreo() != null && this.clienteTiendaRepositorio
                .existsByCorreoAndIdClienteTiendaNot(clienteTienda.getCorreo(), clienteTienda.getIdClienteTienda())) {
            throw new Exception("Ya existe otro cliente con el correo: " + clienteTienda.getCorreo());
        }
        return this.clienteTiendaRepositorio.save(clienteTienda);
    }

    @Override
    public void eliminarClienteTiendaPorId(Long id) {
        this.clienteTiendaRepositorio.deleteById(id);
    }
}
