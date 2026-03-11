package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.CategoriaGastoRegistroDTO;
import com.aseo360.aseo360.modelo.CategoriaGasto;
import com.aseo360.aseo360.servicio.interfaz.ICategoriaGastoServicio;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/categoria/gasto")
public class CategoriaGastoControlador {
    private final ICategoriaGastoServicio categoriaGastoServicio;

    public CategoriaGastoControlador(ICategoriaGastoServicio categoriaGastoServicio) {
        this.categoriaGastoServicio = categoriaGastoServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarCategoriaGasto() {
        try {
            return ResponseEntity.ok(this.categoriaGastoServicio.listarCategoriaGasto());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCategoriaGasto(@Valid @RequestBody CategoriaGastoRegistroDTO dto) {
        try {
            return ResponseEntity.ok(this.categoriaGastoServicio.registrarCategoriaGasto(dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarCategoriaGasto(@PathVariable Long id,
            @Valid @RequestBody CategoriaGastoRegistroDTO dto) {
        try {
            return ResponseEntity.ok(this.categoriaGastoServicio.modificarCategoriaGasto(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable Long id) {
        try {
            this.categoriaGastoServicio.eliminarCategoriaGasto(id);
            return ResponseEntity.ok("Categoria eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
