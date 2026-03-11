package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.MensajeEstadoDTO;
import com.aseo360.aseo360.dto.MensajeRegistroDTO;
import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.repositorio.IClienteRepositorio;
import com.aseo360.aseo360.security.UserLogin;
import com.aseo360.aseo360.servicio.interfaz.IMensajeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/mensaje")
public class MensajeControlador {
    private final IMensajeServicio mensajeServicio;
    private final IClienteRepositorio clienteRepositorio;

    @Autowired
    public MensajeControlador(IMensajeServicio mensajeServicio, IClienteRepositorio clienteRepositorio) {
        this.mensajeServicio = mensajeServicio;
        this.clienteRepositorio = clienteRepositorio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarMensajes() {
        try {
            return ResponseEntity.ok(this.mensajeServicio.listarMensajes());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMensaje(@AuthenticationPrincipal UserLogin userLogin,
            @Valid @RequestBody MensajeRegistroDTO mensajeRegistroDTO) throws Exception {
        try {
            Cliente cliente = this.clienteRepositorio.findByCorreo(userLogin.getUsername())
                    .orElseThrow(() -> new Exception("Error: correo no encontrado"));
            this.mensajeServicio.registrarMensaje(cliente, mensajeRegistroDTO);
            return ResponseEntity.ok("Mensaje enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PatchMapping("/modificar/estado")
    public ResponseEntity<?> modificarEstado(@Valid @RequestBody MensajeEstadoDTO mensajeEstadoDTO) {
        try {
            return ResponseEntity.ok(this.mensajeServicio.modificarEstado(mensajeEstadoDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarMensaje(@PathVariable Long id) {
        try {
            this.mensajeServicio.eliminarMensaje(id);
            return ResponseEntity.ok("Mensaje Eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
