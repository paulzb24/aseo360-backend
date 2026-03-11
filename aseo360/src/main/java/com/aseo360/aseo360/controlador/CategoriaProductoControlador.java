package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.CategoriaProductoRegistroDTO;

import com.aseo360.aseo360.servicio.interfaz.ICategoriaProductoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/categoria")
public class CategoriaProductoControlador {
    private final ICategoriaProductoServicio categoriaProductoServicio;

    @Autowired
    public CategoriaProductoControlador(ICategoriaProductoServicio categoriaProductoServicio) {
        this.categoriaProductoServicio = categoriaProductoServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarCategoriaProducto() {
        try {
            return ResponseEntity.ok(this.categoriaProductoServicio.listarCategoriaProducto());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCategoriaProducto(
            @Valid @RequestBody CategoriaProductoRegistroDTO categoriaProductoDTO) {
        try {
            return ResponseEntity.ok(this.categoriaProductoServicio.registrarCatProducto(categoriaProductoDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarCategoriaProducto(@PathVariable Long id,
            @Valid @RequestBody CategoriaProductoRegistroDTO categoriaProductoDTO) {
        try {
            return ResponseEntity.ok(this.categoriaProductoServicio.modificarCatProducto(id, categoriaProductoDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCategoriaProducto(@PathVariable Long id) {
        try {
            this.categoriaProductoServicio.eliminarCatProductoPorId(id);
            return ResponseEntity.ok("Categoria eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
