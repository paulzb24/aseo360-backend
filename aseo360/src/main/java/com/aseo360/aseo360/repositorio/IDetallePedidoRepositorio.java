package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.DetallePedido;
import com.aseo360.aseo360.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDetallePedidoRepositorio extends JpaRepository<DetallePedido, Long> {
    public List<DetallePedido> findAllByPedido(Pedido pedido);
}
