package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPedidoRepositorio extends JpaRepository<Pedido, Long> {
    public Page<Pedido> findAllByCliente(Cliente cliente, Pageable pageable);
}
