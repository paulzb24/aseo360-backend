package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.modelo.ConfiguracionSistema;
import com.aseo360.aseo360.servicio.implementacion.ConfiguracionSistemaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/configuracion")
public class ConfiguracionSistemaControlador {

    private final ConfiguracionSistemaServicio configuracionServicio;

    @Autowired
    public ConfiguracionSistemaControlador(ConfiguracionSistemaServicio configuracionServicio) {
        this.configuracionServicio = configuracionServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarConfiguraciones() {
        return ResponseEntity.ok(this.configuracionServicio.listarConfiguraciones());
    }

    @GetMapping("/{clave}")
    public ResponseEntity<?> obtenerPorClave(@PathVariable String clave) {
        try {
            return ResponseEntity.ok(this.configuracionServicio.obtenerPorClave(clave));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarConfiguracion(@RequestBody ConfiguracionSistema configuracion) {
        try {
            return ResponseEntity.ok(this.configuracionServicio.registrarConfiguracion(configuracion));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity<?> modificarConfiguracion(@RequestBody ConfiguracionSistema configuracion) {
        try {
            return ResponseEntity.ok(this.configuracionServicio.modificarConfiguracion(configuracion));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarConfiguracion(@PathVariable Long id) {
        try {
            this.configuracionServicio.eliminarConfiguracion(id);
            return ResponseEntity.ok("Configuracion eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
