package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.ProveedorRegistroDTO;

import com.aseo360.aseo360.servicio.interfaz.IProveedorServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/proveedor")
public class ProveedorControlador {
    private final IProveedorServicio proveedorServicio;

    @Autowired
    public ProveedorControlador(IProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarProveedores() {
        try {
            return ResponseEntity.ok(this.proveedorServicio.listarProveedores());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarProveedor(@Valid @RequestBody ProveedorRegistroDTO proveedorDTO) {
        try {
            return ResponseEntity.ok(this.proveedorServicio.registrarProveedor(proveedorDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity<?> modificarProveedor(@Valid @RequestBody ProveedorRegistroDTO proveedorDTO) {
        try {
            return ResponseEntity.ok(this.proveedorServicio.modificarProveedor(proveedorDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable String id) {
        try {
            this.proveedorServicio.eliminarPorId(id);
            return ResponseEntity.ok("¡Eliminación exitosa!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
