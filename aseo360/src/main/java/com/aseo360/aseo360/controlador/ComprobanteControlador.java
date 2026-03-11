package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.ComprobanteManualDTO;
import com.aseo360.aseo360.dto.ComprobanteResponseDTO;
import com.aseo360.aseo360.modelo.Comprobante;
import com.aseo360.aseo360.servicio.interfaz.IComprobanteServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/comprobante")
public class ComprobanteControlador {

    private final IComprobanteServicio comprobanteServicio;

    @Autowired
    public ComprobanteControlador(IComprobanteServicio comprobanteServicio) {
        this.comprobanteServicio = comprobanteServicio;
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<Page<Comprobante>> listarComprobantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<Comprobante> comprobantes = comprobanteServicio.listarComprobantes(PageRequest.of(page, size));
        return ResponseEntity.ok(comprobantes);
    }

    @PostMapping("/emitir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> emitirComprobanteManual(@Valid @RequestBody ComprobanteManualDTO dto) {
        try {
            ComprobanteResponseDTO resultado = comprobanteServicio.emitirComprobanteManual(dto);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al emitir comprobante: " + e.getMessage());
        }
    }

    @PutMapping("/anular/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> anularComprobante(@PathVariable Long id) {
        try {
            ComprobanteResponseDTO resultado = comprobanteServicio.anularComprobante(id);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al anular comprobante: " + e.getMessage());
        }
    }

    @PostMapping("/consultar-estado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> consultarEstado(@PathVariable Long id) {
        try {
            ComprobanteResponseDTO resultado = comprobanteServicio.consultarEstado(id);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al consultar estado: " + e.getMessage());
        }
    }

    @PostMapping("/enviar-guia")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public ResponseEntity<?> enviarGuiaRemision(@Valid @RequestBody com.aseo360.aseo360.dto.GuiaRemisionDTO dto) {
        try {
            java.util.Map<String, Object> resultado = comprobanteServicio.emitirGuiaRemision(dto);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al emitir guía de remisión: " + e.getMessage());
        }
    }
}
