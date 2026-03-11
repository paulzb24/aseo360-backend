package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Comprobante;
import com.aseo360.aseo360.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IComprobanteRepositorio extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByVenta(Venta venta);

    Page<Comprobante> findAllByOrderByFechaEmisionDesc(Pageable pageable);
}
