package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.AdquisicionEstadoDTO;
import com.aseo360.aseo360.dto.AdquisicionRegistroDTO;
import com.aseo360.aseo360.dto.DetalleAdquisicionDTO;
import com.aseo360.aseo360.modelo.Adquisicion;
import com.aseo360.aseo360.modelo.DetalleAdquisicion;
import com.aseo360.aseo360.repositorio.IAdquisicionRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IAdquisicionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdquisicionServicio implements IAdquisicionServicio {

    private final IAdquisicionRepositorio adquisicionRepositorio;

    @Autowired
    public AdquisicionServicio(IAdquisicionRepositorio adquisicionRepositorio) {
        this.adquisicionRepositorio = adquisicionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Adquisicion> listarAdquisiciones() {
        return adquisicionRepositorio.findAllByOrderByFechaHoraDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Adquisicion buscarPorId(Long id) {
        return adquisicionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Adquisición no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Adquisicion registrarAdquisicion(AdquisicionRegistroDTO dto) {
        Adquisicion adquisicion = new Adquisicion();
        adquisicion.setFechaHora(LocalDateTime.now());
        adquisicion.setDescripcion(dto.getDescripcion());
        adquisicion.setSolicitadoPor(dto.getSolicitadoPor());
        adquisicion.setPrioridad(dto.getPrioridad());
        adquisicion.setArea(dto.getArea());
        adquisicion.setEstado("EN_PROCESO");

        List<DetalleAdquisicion> detalles = mapearDetalles(dto.getItems(), adquisicion);
        adquisicion.setDetalles(detalles);

        BigDecimal totalEstimado = detalles.stream()
                .map(DetalleAdquisicion::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        adquisicion.setTotalEstimado(totalEstimado);

        return adquisicionRepositorio.save(adquisicion);
    }

    @Override
    @Transactional
    public Adquisicion modificarAdquisicion(Long id, AdquisicionRegistroDTO dto) {
        Adquisicion adquisicion = buscarPorId(id);
        adquisicion.setDescripcion(dto.getDescripcion());
        adquisicion.setSolicitadoPor(dto.getSolicitadoPor());
        adquisicion.setPrioridad(dto.getPrioridad());
        adquisicion.setArea(dto.getArea());

        // Limpiar detalles anteriores y crear nuevos
        adquisicion.getDetalles().clear();
        List<DetalleAdquisicion> nuevosDetalles = mapearDetalles(dto.getItems(), adquisicion);
        adquisicion.getDetalles().addAll(nuevosDetalles);

        BigDecimal totalEstimado = nuevosDetalles.stream()
                .map(DetalleAdquisicion::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        adquisicion.setTotalEstimado(totalEstimado);

        return adquisicionRepositorio.save(adquisicion);
    }

    @Override
    @Transactional
    public Adquisicion cambiarEstado(Long id, AdquisicionEstadoDTO dto) {
        Adquisicion adquisicion = buscarPorId(id);
        adquisicion.setEstado(dto.getEstado());
        return adquisicionRepositorio.save(adquisicion);
    }

    @Override
    @Transactional
    public void eliminarAdquisicion(Long id) {
        Adquisicion adquisicion = buscarPorId(id);
        adquisicionRepositorio.delete(adquisicion);
    }

    private List<DetalleAdquisicion> mapearDetalles(List<DetalleAdquisicionDTO> items, Adquisicion adquisicion) {
        List<DetalleAdquisicion> detalles = new ArrayList<>();
        if (items != null) {
            for (DetalleAdquisicionDTO item : items) {
                DetalleAdquisicion detalle = new DetalleAdquisicion();
                detalle.setAdquisicion(adquisicion);
                detalle.setNombreProducto(item.getNombreProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecioUnitario());
                BigDecimal precioTotal = item.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(item.getCantidad()));
                detalle.setPrecioTotal(precioTotal);
                detalles.add(detalle);
            }
        }
        return detalles;
    }
}
