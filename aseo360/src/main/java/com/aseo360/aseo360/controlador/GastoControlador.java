package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.GastoRegistroDTO;
import com.aseo360.aseo360.servicio.interfaz.IGastoServicio;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/gasto")
public class GastoControlador {
    private final IGastoServicio gastoServicio;

    @Autowired
    public GastoControlador(IGastoServicio gastoServicio) {
        this.gastoServicio = gastoServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarGastos() {
        try {
            return ResponseEntity.ok(this.gastoServicio.listarGastos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarGastoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.gastoServicio.buscarGastoPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarGasto(@Valid @RequestBody GastoRegistroDTO dto) {
        try {
            return ResponseEntity.ok(this.gastoServicio.registrarGasto(dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarGasto(@PathVariable Long id, @Valid @RequestBody GastoRegistroDTO dto) {
        try {
            return ResponseEntity.ok(this.gastoServicio.modificarGasto(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarGasto(@PathVariable Long id) {
        try {
            this.gastoServicio.eliminarGastoPorId(id);
            return ResponseEntity.ok("Gasto eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
