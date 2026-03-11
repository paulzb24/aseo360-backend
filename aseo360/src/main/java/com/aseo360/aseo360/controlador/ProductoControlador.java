package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.ProductoRegistroDTO;
import com.aseo360.aseo360.servicio.interfaz.IProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/producto")
public class ProductoControlador {

    private final IProductoServicio productoServicio;

    @Autowired
    public ProductoControlador(IProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','ALMACENERO')")
    public ResponseEntity<?> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(this.productoServicio.listarProductos(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/listar/disponibles")
    public ResponseEntity<?> listarProductoDisponibles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(this.productoServicio.listarProductosDisponibles(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENERO')")
    public ResponseEntity<?> registrarProducto(@Valid @RequestBody ProductoRegistroDTO productoRegistroDTO) {
        try {
            return ResponseEntity.ok(this.productoServicio.registrarProducto(productoRegistroDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENERO')")
    public ResponseEntity<?> actualizarProducto(@Valid @RequestBody ProductoRegistroDTO productoRegistroDTO) {
        try {
            return ResponseEntity.ok(this.productoServicio.modificarProducto(productoRegistroDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ALMACENERO')")
    public ResponseEntity<?> eliminarProducto(@PathVariable String id) {
        try {
            this.productoServicio.eliminarPorId(id);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Producto eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
