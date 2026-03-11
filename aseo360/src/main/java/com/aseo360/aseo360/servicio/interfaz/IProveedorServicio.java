package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.ProveedorRegistroDTO;
import com.aseo360.aseo360.modelo.Proveedor;

import java.util.List;

public interface IProveedorServicio {
    public List<Proveedor> listarProveedores();

    public Proveedor registrarProveedor(ProveedorRegistroDTO proveedorDTO) throws Exception;

    public Proveedor modificarProveedor(ProveedorRegistroDTO proveedorDTO) throws Exception;

    public Proveedor buscarProveedorPorId(String id) throws Exception;

    public void eliminarPorId(String id);
}
