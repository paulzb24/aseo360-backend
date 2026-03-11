package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.ReporteDashboardDTO;
import com.aseo360.aseo360.servicio.interfaz.IReporteServicio;
import com.aseo360.aseo360.repositorio.IVentaRepositorio;
import com.aseo360.aseo360.repositorio.IDetalleVentaRepositorio;
import com.aseo360.aseo360.repositorio.IGastoRepositorio;
import com.aseo360.aseo360.repositorio.IAsistenciaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Collectors;

@Service
public class ReporteServicioImpl implements IReporteServicio {

        @Autowired
        private IVentaRepositorio ventaRepositorio;

        @Autowired
        private IDetalleVentaRepositorio detalleVentaRepositorio;

        @Autowired
        private IGastoRepositorio gastoRepositorio;

        @Autowired
        private IAsistenciaRepositorio asistenciaRepositorio;

        @Override
        @Transactional(readOnly = true)
        public ReporteDashboardDTO obtenerDashboard(LocalDate fechaInicio, LocalDate fechaFin) {
                // Asegurar que "hoy" se evalúe estrictamente en hora de Perú (America/Lima)
                LocalDate hoy = LocalDate.now(java.time.ZoneId.of("America/Lima"));
                LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate inicioMes = hoy.withDayOfMonth(1);

                ReporteDashboardDTO dto = new ReporteDashboardDTO();

                // 1. Totales Generales
                dto.setTotalDiario(seguro(ventaRepositorio.obtenerTotalVentasRango(hoy, hoy)));
                dto.setTotalSemanal(seguro(ventaRepositorio.obtenerTotalVentasRango(inicioSemana, hoy)));
                dto.setTotalMensual(seguro(ventaRepositorio.obtenerTotalVentasRango(inicioMes, hoy)));
                dto.setTotalRangoSeleccionado(seguro(ventaRepositorio.obtenerTotalVentasRango(fechaInicio, fechaFin)));

                // 2. Metodos de Pago (Mapeo de Projections)
                dto.setMetodosDiario(ventaRepositorio.obtenerVentasPorMetodo(hoy, hoy).stream()
                                .map(p -> new ReporteDashboardDTO.VentasPorMetodo(p.getMetodo(), p.getTotal()))
                                .collect(Collectors.toList()));

                dto.setMetodosSemanal(ventaRepositorio.obtenerVentasPorMetodo(inicioSemana, hoy).stream()
                                .map(p -> new ReporteDashboardDTO.VentasPorMetodo(p.getMetodo(), p.getTotal()))
                                .collect(Collectors.toList()));

                dto.setMetodosMensual(ventaRepositorio.obtenerVentasPorMetodo(inicioMes, hoy).stream()
                                .map(p -> new ReporteDashboardDTO.VentasPorMetodo(p.getMetodo(), p.getTotal()))
                                .collect(Collectors.toList()));

                // 3. Rankings en el Rango Seleccionado con Limit de la paginacion (Mapeo de
                // Projections)
                dto.setTopProductos(detalleVentaRepositorio
                                .obtenerTopProductos(fechaInicio, fechaFin, PageRequest.of(0, 10)).stream()
                                .map(p -> new ReporteDashboardDTO.TopProducto(p.getNombre(), p.getCantidadVendida(),
                                                p.getIngresos()))
                                .collect(Collectors.toList()));

                dto.setTopClientes(ventaRepositorio.obtenerTopClientes(fechaInicio, fechaFin, PageRequest.of(0, 5))
                                .stream()
                                .map(p -> new ReporteDashboardDTO.TopCliente(p.getNombre(), p.getCantidadVentas(),
                                                p.getTotalComprado()))
                                .collect(Collectors.toList()));

                dto.setTopTiposGasto(gastoRepositorio
                                .obtenerTopTiposGasto(fechaInicio, fechaFin, PageRequest.of(0, 5)).stream()
                                .map(p -> new ReporteDashboardDTO.TopTipoGasto(p.getTipoGasto(), p.getCantidadGastos(),
                                                p.getTotalGastado()))
                                .collect(Collectors.toList()));

                dto.setTopEmpleados(asistenciaRepositorio
                                .obtenerTopEmpleadosPuntuales(fechaInicio, fechaFin, PageRequest.of(0, 5)).stream()
                                .map(p -> new ReporteDashboardDTO.TopEmpleado(p.getNombre(),
                                                p.getAsistenciasPuntuales()))
                                .collect(Collectors.toList()));

                return dto;
        }

        private BigDecimal seguro(BigDecimal valor) {
                return valor != null ? valor : BigDecimal.ZERO;
        }
}
