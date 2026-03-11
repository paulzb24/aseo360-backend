package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.modelo.Sede;
import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.TipoInventario;
import com.aseo360.aseo360.repositorio.ISedeRepositorio;
import com.aseo360.aseo360.repositorio.IInventarioRepositorio;
import com.aseo360.aseo360.servicio.interfaz.ISedeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeServicio implements ISedeServicio {
    private final ISedeRepositorio sedeRepositorio;
    private final IInventarioRepositorio inventarioRepositorio;

    @Autowired
    public SedeServicio(ISedeRepositorio sedeRepositorio, IInventarioRepositorio inventarioRepositorio) {
        this.sedeRepositorio = sedeRepositorio;
        this.inventarioRepositorio = inventarioRepositorio;
    }

    @Override
    public List<Sede> listarSedes() {
        return this.sedeRepositorio.findAll();
    }

    @Override
    public Sede registrarSede(Sede sede) throws Exception {
        if (this.sedeRepositorio.existsByNombre(sede.getNombre())) {
            throw new Exception("Ya existe una sede con el nombre: " + sede.getNombre());
        }
        Sede nuevaSede = this.sedeRepositorio.save(sede);

        Inventario principal = new Inventario();
        principal.setNombre("Almacén Principal (" + nuevaSede.getNombre() + ")");
        principal.setTipo(TipoInventario.PRINCIPAL);
        principal.setSede(nuevaSede);

        Inventario tienda = new Inventario();
        tienda.setNombre("Punto de Venta (" + nuevaSede.getNombre() + ")");
        tienda.setTipo(TipoInventario.TIENDA);
        tienda.setSede(nuevaSede);

        this.inventarioRepositorio.saveAll(List.of(principal, tienda));

        return nuevaSede;
    }

    @Override
    public Sede buscarSedePorId(Long id) throws Exception {
        return this.sedeRepositorio.findById(id).orElseThrow(() -> new Exception("Error: sede no encontrado"));
    }

    @Override
    public void eliminarSedePorId(Long id) {
        this.sedeRepositorio.deleteById(id);
    }
}
