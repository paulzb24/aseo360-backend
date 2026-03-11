package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.PedidoEstadoDTO;
import com.aseo360.aseo360.dto.PedidoRegistroDTO;
import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.modelo.DetallePedido;
import com.aseo360.aseo360.modelo.Pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IPedidoServicio {
    public Page<Pedido> listarPedidos(Pageable pageable);

    public List<DetallePedido> listarDetallesPorPedioId(Long id) throws Exception;

    public Pedido registrarPedido(PedidoRegistroDTO pedidoRegistroDTO) throws Exception;

    public Pedido buscarPedidoPorId(Long id) throws Exception;

    public Pedido anularPedido(Long id) throws Exception;

    public Pedido modificarEstadoPedido(PedidoEstadoDTO pedidoEstadoDTO) throws Exception;

    public Page<Pedido> listarPedidoPorCliente(Cliente cliente, Pageable pageable);
}
