package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.EmpleadoRegistroDTO;
import com.aseo360.aseo360.servicio.interfaz.IEmpleadoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/empleados")
public class EmpleadoControlador {
    private final IEmpleadoServicio empleadoServicio;

    @Autowired
    public EmpleadoControlador(IEmpleadoServicio empleadoServicio) {
        this.empleadoServicio = empleadoServicio;
    }

    @GetMapping
    public ResponseEntity<?> listarEmpleados() {
        return ResponseEntity.ok(this.empleadoServicio.listarEmpleados());
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarEmpleado(@Valid @RequestBody EmpleadoRegistroDTO empleadoRegistroDTO)
            throws Exception {
        return ResponseEntity.ok(this.empleadoServicio.registrarEmpleado(empleadoRegistroDTO));
    }

    @PutMapping("/modificar")
    public ResponseEntity<?> modificarEmpleado(@Valid @RequestBody EmpleadoRegistroDTO empleadoModificar) {
        try {
            return ResponseEntity.ok(this.empleadoServicio.modificarEmpleado(empleadoModificar));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarEmpleado(@PathVariable Long id) {
        try {
            this.empleadoServicio.eliminarPorId(id);
            return ResponseEntity.ok("Rol eliminado correctamete");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
