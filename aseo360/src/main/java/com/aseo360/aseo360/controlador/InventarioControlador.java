package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.InventarioRegistroDTO;
import com.aseo360.aseo360.dto.TrasladoInventarioDTO;
import com.aseo360.aseo360.dto.IngresoMercaderiaDTO;
import com.aseo360.aseo360.dto.SalidaMercaderiaDTO;
import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.InventarioProducto;
import com.aseo360.aseo360.servicio.interfaz.IInventarioServicio;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventarios")
@CrossOrigin(origins = "*") // Ajustar según configuración de seguridad
public class InventarioControlador {

    private final IInventarioServicio inventarioServicio;

    @Autowired
    public InventarioControlador(IInventarioServicio inventarioServicio) {
        this.inventarioServicio = inventarioServicio;
    }

    @PostMapping
    public ResponseEntity<?> crearInventario(@Valid @RequestBody InventarioRegistroDTO inventarioRegistroDTO) {
        try {
            return ResponseEntity.ok(inventarioServicio.crearInventario(inventarioRegistroDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Inventario>> listarInventarios() {
        return ResponseEntity.ok(inventarioServicio.listarInventarios());
    }

    @GetMapping("/sede/{idSede}")
    public ResponseEntity<List<Inventario>> listarPorSede(@PathVariable Long idSede) {
        return ResponseEntity.ok(inventarioServicio.listarInventariosPorSede(idSede));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inventarioServicio.obtenerInventarioPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<InventarioProducto>> listarProductosPorInventario(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inventarioServicio.listarProductosPorInventario(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/stock/{idProducto}")
    public ResponseEntity<InventarioProducto> obtenerStock(@PathVariable Long id, @PathVariable String idProducto) {
        try {
            return ResponseEntity.ok(inventarioServicio.obtenerStockProducto(id, idProducto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/ingreso")
    public ResponseEntity<?> registrarIngreso(@Valid @RequestBody IngresoMercaderiaDTO ingresoDTO) {
        try {
            List<String> alertas = inventarioServicio.registrarIngresoMercaderia(ingresoDTO);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Ingreso de mercadería registrado con éxito",
                    "alertas", alertas));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/salida")
    public ResponseEntity<?> registrarSalida(@Valid @RequestBody SalidaMercaderiaDTO salidaDTO) {
        try {
            inventarioServicio.registrarSalidaMercaderia(salidaDTO);
            return ResponseEntity.ok(Map.of("mensaje", "Salida de mercadería registrada con éxito"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/traslado")
    public ResponseEntity<?> realizarTraslado(@Valid @RequestBody TrasladoInventarioDTO trasladoDTO) {
        try {
            inventarioServicio.procesarTraslado(trasladoDTO);
            return ResponseEntity.ok(Map.of("mensaje", "Solicitud de traslado registrada como EN_PROCESO."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/traslados")
    public ResponseEntity<?> listarTraslados() {
        try {
            return ResponseEntity.ok(inventarioServicio.listarTraslados());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/traslados/{id}/estado")
    public ResponseEntity<?> cambiarEstadoTraslado(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String nuevoEstado = payload.get("estado");
            String usuario = payload.get("usuario");
            inventarioServicio.cambiarEstadoTraslado(id, nuevoEstado, usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Estado del traslado actualizado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint auxiliar para inicializar inventario principal y tienda de una sede
    // Esto es util si no se tiene un flujo de creacion automatico aun
    @PostMapping("/inicializar/{idSede}")
    public ResponseEntity<?> inicializarInventariosSede(@PathVariable Long idSede) {
        // Logica simple: crear inventarios si no existen
        // Como IInventarioServicio no expone esto directamente, lo hacemos aqui o lo
        // agregamos al servicio
        // Por brevedad, asumimos que el usuario lo hara manualmente con POST
        // /api/inventarios
        // O implementamos una logica minima aqui extendiendo el servicio.
        return ResponseEntity
                .ok(Map.of("mensaje", "Funcionalidad pendiente de implementar en servicio, use POST /api/inventarios"));
    }

    @PostMapping("/{id}/sincronizar")
    public ResponseEntity<?> sincronizarCatalogo(@PathVariable Long id) {
        try {
            inventarioServicio.sincronizarProductosCero(id);
            return ResponseEntity.ok(Map.of("mensaje", "Catálogo sincronizado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/kardex")
    public ResponseEntity<?> listarKardex(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        return ResponseEntity.ok(inventarioServicio.listarKardex(id, pageable));
    }

    // Endpoint global: lista todos los movimientos de kardex de todos los
    // inventarios
    @GetMapping("/kardex/global")
    public ResponseEntity<?> listarKardexGlobal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
            return ResponseEntity.ok(inventarioServicio.listarKardexGlobal(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
