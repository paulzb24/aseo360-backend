package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IProductoRepositorio extends JpaRepository<Producto, String> {
    public Page<Producto> findAllByEstado(String estado, Pageable pageable);
}
