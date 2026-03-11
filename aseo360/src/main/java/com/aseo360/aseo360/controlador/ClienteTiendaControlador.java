package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.modelo.ClienteTienda;
import com.aseo360.aseo360.servicio.interfaz.IClienteTiendaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/clientetienda")
public class ClienteTiendaControlador {
    private final IClienteTiendaServicio clienteTiendaServicio;

    @Autowired
    public ClienteTiendaControlador(IClienteTiendaServicio clienteTiendaServicio){
        this.clienteTiendaServicio = clienteTiendaServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClienteTienda(){
        try {
            return ResponseEntity.ok(this.clienteTiendaServicio.listarClienteTienda());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @GetMapping("/buscar/{dni}/dni")
    public ResponseEntity<?> buscarPorDni(@PathVariable String dni){
        try {
            return ResponseEntity.ok(this.clienteTiendaServicio.buscarPorDNI(dni));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarClienteTienda(@RequestBody ClienteTienda clienteTienda){
        try {
            return ResponseEntity.ok(this.clienteTiendaServicio.registrarClienteTienda(clienteTienda));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity<?> modificarClienteTienda(@RequestBody ClienteTienda clienteTienda){
        try {
            return ResponseEntity.ok(this.clienteTiendaServicio.modificarClienteTienda(clienteTienda));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarClienteTienda(@PathVariable Long id){
        try{
            this.clienteTiendaServicio.eliminarClienteTiendaPorId(id);
            return ResponseEntity.ok("Exito!, eliminado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
