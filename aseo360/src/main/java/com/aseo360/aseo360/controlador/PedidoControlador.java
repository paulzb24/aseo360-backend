package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.PedidoEstadoDTO;
import com.aseo360.aseo360.dto.PedidoRegistroDTO;
import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.repositorio.IClienteRepositorio;
import com.aseo360.aseo360.security.UserLogin;
import com.aseo360.aseo360.servicio.interfaz.IPedidoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/pedido")
public class PedidoControlador {
    private final IPedidoServicio pedidoServicio;
    private final IClienteRepositorio clienteRepositorio;

    @Autowired
    public PedidoControlador(IPedidoServicio pedidoServicio, IClienteRepositorio clienteRepositorio) {
        this.pedidoServicio = pedidoServicio;
        this.clienteRepositorio = clienteRepositorio;
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(this.pedidoServicio.listarPedidos(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/listar/pedido/cliente")
    public ResponseEntity<?> listarPedidosPorCliente(@AuthenticationPrincipal UserLogin userLogin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Cliente cliente = this.clienteRepositorio.findByCorreo(userLogin.getUsername())
                    .orElseThrow(() -> new Exception("Error: cliente no encontrado"));
            return ResponseEntity.ok(this.pedidoServicio.listarPedidoPorCliente(cliente, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/listar/detalles/{id}")
    public ResponseEntity<?> listarDetallePorPedido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.pedidoServicio.listarDetallesPorPedioId(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPedido(@AuthenticationPrincipal UserLogin userLogin,
            @Valid @RequestBody PedidoRegistroDTO pedidoRegistroDTO) throws Exception {
        try {
            String correo = userLogin.getUsername();
            Cliente cliente = this.clienteRepositorio.findByCorreo(correo)
                    .orElseThrow(() -> new Exception("Error: cliente no encontrado"));
            pedidoRegistroDTO.setIdCliente(cliente.getIdCliente());
            return ResponseEntity.ok(this.pedidoServicio.registrarPedido(pedidoRegistroDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PatchMapping("/anular/{id}/pedido")
    public ResponseEntity<?> anularPedido(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.pedidoServicio.anularPedido(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PatchMapping("/modificar/estado")
    public ResponseEntity<?> modificarEstadoPedido(@Valid @RequestBody PedidoEstadoDTO pedidoEstadoDTO) {
        try {
            return ResponseEntity.ok(this.pedidoServicio.modificarEstadoPedido(pedidoEstadoDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
