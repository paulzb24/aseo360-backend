package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.InventarioRegistroDTO;
import com.aseo360.aseo360.dto.TrasladoInventarioDTO;
import com.aseo360.aseo360.dto.SalidaMercaderiaDTO;
import com.aseo360.aseo360.modelo.Inventario;
import com.aseo360.aseo360.modelo.InventarioProducto;
import com.aseo360.aseo360.modelo.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.aseo360.aseo360.modelo.KardexInventario;

import java.util.List;

public interface IInventarioServicio {
        Inventario crearInventario(InventarioRegistroDTO inventarioRegistroDTO) throws Exception;

        List<Inventario> listarInventariosPorSede(Long idSede);

        List<Inventario> listarInventarios();

        Inventario obtenerInventarioPorId(Long id) throws Exception;

        List<InventarioProducto> listarProductosPorInventario(Long idInventario) throws Exception;

        void sincronizarProductosCero(Long idInventario) throws Exception;

        // Gestión de Stock
        List<String> registrarIngresoMercaderia(com.aseo360.aseo360.dto.IngresoMercaderiaDTO dto) throws Exception;

        void registrarSalidaMercaderia(SalidaMercaderiaDTO dto) throws Exception;

        InventarioProducto obtenerStockProducto(Long idInventario, String idProducto) throws Exception;

        void actualizarStock(Long idInventario, String idProducto, Integer cantidad, String tipoMovimiento,
                        String referencia, String usuario) throws Exception;

        void procesarTraslado(TrasladoInventarioDTO trasladoDTO) throws Exception;

        void cambiarEstadoTraslado(Long idTraslado, String nuevoEstado, String usuario) throws Exception;

        List<com.aseo360.aseo360.modelo.TrasladoInventario> listarTraslados();

        // Inicialización
        void inicializarStockProducto(Producto producto, Long idInventarioSeleccionado, Integer cantidadInicial,
                        String idProveedor);

        Page<KardexInventario> listarKardex(Long idInventario, Pageable pageable);

        // Lista todos los movimientos de kardex de todos los inventarios
        Page<KardexInventario> listarKardexGlobal(Pageable pageable);
}
