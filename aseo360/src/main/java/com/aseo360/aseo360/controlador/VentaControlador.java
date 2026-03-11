package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.VentaRegistroDTO;
import com.aseo360.aseo360.servicio.interfaz.IVentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/venta")
public class VentaControlador {
    private final IVentaServicio ventaServicio;

    @Autowired
    public VentaControlador(IVentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> listarVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idVenta"));
            return ResponseEntity.ok(this.ventaServicio.listarVentas(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/listar/detalles/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> listarDetallePorVenta(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.ventaServicio.listarDetallesPorVentaId(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaRegistroDTO ventaRegistroDTO) {
        try {

            return ResponseEntity.ok(this.ventaServicio.registrarVenta(ventaRegistroDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PatchMapping("/anular/{id}/venta")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> anularVenta(@PathVariable Long id) {
        try {
            this.ventaServicio.anularVenta(id);
            return ResponseEntity.ok("Venta con id " + id + "ha sido anulado y el stock revertido.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
