package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.modelo.Aroma;
import com.aseo360.aseo360.servicio.interfaz.IAromaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/aroma")
public class AromaControlador {

    @Autowired
    private IAromaServicio aromaServicio;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAroma(@RequestBody Aroma aroma) {
        try {
            return ResponseEntity.ok(this.aromaServicio.registrarAroma(aroma));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarAromas() {
        try {
            return ResponseEntity.ok(this.aromaServicio.listarAromas());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarAromas(@PathVariable Long id) {
        try {
            this.aromaServicio.eliminarAromaPorId(id);
            return ResponseEntity.ok("Eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
