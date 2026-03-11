package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.GastoRegistroDTO;
import com.aseo360.aseo360.modelo.Gasto;
import com.aseo360.aseo360.repositorio.IGastoRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IGastoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GastoServicio implements IGastoServicio {

    private final IGastoRepositorio gastoRepositorio;

    @Autowired
    public GastoServicio(IGastoRepositorio gastoRepositorio) {
        this.gastoRepositorio = gastoRepositorio;
    }

    @Override
    public List<Gasto> listarGastos() {
        return this.gastoRepositorio.findAll();
    }

    @Override
    public Gasto registrarGasto(GastoRegistroDTO dto) throws Exception {
        Gasto gasto = new Gasto();
        gasto.setTipoGasto(dto.getTipoGasto());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(dto.getMonto());
        gasto.setFecha(dto.getFecha());
        gasto.setMetodoPago(dto.getMetodoPago());

        return this.gastoRepositorio.save(gasto);
    }

    @Override
    public Gasto buscarGastoPorId(Long id) throws Exception {
        return this.gastoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Gasto no encontrado"));
    }

    @Override
    public Gasto modificarGasto(Long id, GastoRegistroDTO dto) throws Exception {
        Gasto gasto = this.gastoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Gasto no encontrado"));

        gasto.setTipoGasto(dto.getTipoGasto());
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setMonto(dto.getMonto());
        gasto.setFecha(dto.getFecha());
        gasto.setMetodoPago(dto.getMetodoPago());

        return this.gastoRepositorio.save(gasto);
    }

    @Override
    public void eliminarGastoPorId(Long id) {
        this.gastoRepositorio.deleteById(id);
    }
}
