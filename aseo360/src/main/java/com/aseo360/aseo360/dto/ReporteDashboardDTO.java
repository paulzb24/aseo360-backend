package com.aseo360.aseo360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDashboardDTO {

    // Totales globales (Independientes del rango, calculados relativos a HOY)
    private BigDecimal totalDiario;
    private BigDecimal totalSemanal;
    private BigDecimal totalMensual;

    // Ventas por Método de Pago (Relativos a HOY)
    private List<VentasPorMetodo> metodosDiario;
    private List<VentasPorMetodo> metodosSemanal;
    private List<VentasPorMetodo> metodosMensual;

    // Totales y Rankings en el RANGO SELECCIONADO por el usuario
    private BigDecimal totalRangoSeleccionado;
    private List<TopProducto> topProductos; // Top 10
    private List<TopCliente> topClientes; // Top 5
    private List<TopTipoGasto> topTiposGasto; // Top 5
    private List<TopEmpleado> topEmpleados; // Top 5

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentasPorMetodo {
        private String metodo;
        private BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProducto {
        private String nombre;
        private Long cantidadVendida;
        private BigDecimal ingresos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCliente {
        private String nombre;
        private Long cantidadVentas;
        private BigDecimal totalComprado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopTipoGasto {
        private String tipoGasto;
        private Long cantidadGastos;
        private BigDecimal totalGastado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopEmpleado {
        private String nombre;
        private Long asistenciasPuntuales;
    }
}
