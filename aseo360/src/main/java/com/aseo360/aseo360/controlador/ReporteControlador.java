package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.ReporteDashboardDTO;
import com.aseo360.aseo360.servicio.interfaz.IReporteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {

    @Autowired
    private IReporteServicio reporteServicio;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<ReporteDashboardDTO> obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si no vienen fechas, por defecto el último mes
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusDays(30);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDashboardDTO dashboard = reporteServicio.obtenerDashboard(fechaInicio, fechaFin);
        return ResponseEntity.ok(dashboard);
    }
}
