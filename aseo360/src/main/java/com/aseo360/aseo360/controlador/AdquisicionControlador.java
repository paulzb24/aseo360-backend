package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.AdquisicionEstadoDTO;
import com.aseo360.aseo360.dto.AdquisicionRegistroDTO;
import com.aseo360.aseo360.modelo.Adquisicion;
import com.aseo360.aseo360.servicio.interfaz.IAdquisicionServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adquisicion")
@CrossOrigin(origins = "*")
public class AdquisicionControlador {

    private final IAdquisicionServicio adquisicionServicio;

    @Autowired
    public AdquisicionControlador(IAdquisicionServicio adquisicionServicio) {
        this.adquisicionServicio = adquisicionServicio;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Adquisicion>> listar() {
        return ResponseEntity.ok(adquisicionServicio.listarAdquisiciones());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Adquisicion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(adquisicionServicio.buscarPorId(id));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Adquisicion> registrar(@Valid @RequestBody AdquisicionRegistroDTO dto) {
        Adquisicion adquisicion = adquisicionServicio.registrarAdquisicion(dto);
        return new ResponseEntity<>(adquisicion, HttpStatus.CREATED);
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<Adquisicion> modificar(@PathVariable Long id,
            @Valid @RequestBody AdquisicionRegistroDTO dto) {
        return ResponseEntity.ok(adquisicionServicio.modificarAdquisicion(id, dto));
    }

    @PatchMapping("/estado/{id}")
    public ResponseEntity<Adquisicion> cambiarEstado(@PathVariable Long id,
            @Valid @RequestBody AdquisicionEstadoDTO dto) {
        return ResponseEntity.ok(adquisicionServicio.cambiarEstado(id, dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        adquisicionServicio.eliminarAdquisicion(id);
        return ResponseEntity.noContent().build();
    }
}
