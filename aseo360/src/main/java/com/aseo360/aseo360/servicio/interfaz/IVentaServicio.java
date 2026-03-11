package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.VentaRegistroDTO;
import com.aseo360.aseo360.dto.VentaResponseDTO;
import com.aseo360.aseo360.modelo.DetalleVenta;
import com.aseo360.aseo360.modelo.Venta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IVentaServicio {
    public Page<Venta> listarVentas(Pageable pageable);

    public List<DetalleVenta> listarDetallesPorVentaId(Long id) throws Exception;

    public VentaResponseDTO registrarVenta(VentaRegistroDTO ventaRegistroDTO) throws Exception;

    public Venta buscarVentaPorId(Long id) throws Exception;

    public Venta anularVenta(Long id) throws Exception;

}
