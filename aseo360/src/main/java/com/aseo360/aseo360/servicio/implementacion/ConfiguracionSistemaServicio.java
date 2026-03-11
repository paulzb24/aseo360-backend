package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.modelo.ConfiguracionSistema;
import com.aseo360.aseo360.repositorio.IConfiguracionSistemaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfiguracionSistemaServicio {

    private final IConfiguracionSistemaRepositorio configuracionRepositorio;

    @Autowired
    public ConfiguracionSistemaServicio(IConfiguracionSistemaRepositorio configuracionRepositorio) {
        this.configuracionRepositorio = configuracionRepositorio;
    }

    // Listar todas las configuraciones
    public List<ConfiguracionSistema> listarConfiguraciones() {
        return this.configuracionRepositorio.findAll();
    }

    // Obtener el valor de una configuracion por su clave
    public String obtenerValor(String clave) throws Exception {
        ConfiguracionSistema config = this.configuracionRepositorio.findByClave(clave)
                .orElseThrow(() -> new Exception("No se encontro la configuracion: " + clave));
        return config.getValor();
    }

    // Obtener la entidad completa por clave
    public ConfiguracionSistema obtenerPorClave(String clave) throws Exception {
        return this.configuracionRepositorio.findByClave(clave)
                .orElseThrow(() -> new Exception("No se encontro la configuracion: " + clave));
    }

    // Registrar una nueva configuracion
    public ConfiguracionSistema registrarConfiguracion(ConfiguracionSistema configuracion) throws Exception {
        if (this.configuracionRepositorio.existsByClave(configuracion.getClave())) {
            throw new Exception("Ya existe una configuracion con la clave: " + configuracion.getClave());
        }
        return this.configuracionRepositorio.save(configuracion);
    }

    // Modificar una configuracion existente
    public ConfiguracionSistema modificarConfiguracion(ConfiguracionSistema configuracion) throws Exception {
        if (configuracion.getIdConfiguracion() == null) {
            throw new Exception("El id de la configuracion es obligatorio para modificar");
        }
        if (this.configuracionRepositorio.existsByClaveAndIdConfiguracionNot(
                configuracion.getClave(), configuracion.getIdConfiguracion())) {
            throw new Exception("Ya existe otra configuracion con la clave: " + configuracion.getClave());
        }
        return this.configuracionRepositorio.save(configuracion);
    }

    // Eliminar una configuracion
    public void eliminarConfiguracion(Long id) throws Exception {
        if (!this.configuracionRepositorio.existsById(id)) {
            throw new Exception("No se encontro la configuracion con id: " + id);
        }
        this.configuracionRepositorio.deleteById(id);
    }
}
