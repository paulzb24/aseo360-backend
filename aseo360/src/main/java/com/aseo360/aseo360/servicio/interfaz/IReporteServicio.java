package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.ReporteDashboardDTO;
import java.time.LocalDate;

public interface IReporteServicio {
    ReporteDashboardDTO obtenerDashboard(LocalDate fechaInicio, LocalDate fechaFin);
}
