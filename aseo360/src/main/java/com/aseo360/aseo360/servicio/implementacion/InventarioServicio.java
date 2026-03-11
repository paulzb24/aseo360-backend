package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.InventarioRegistroDTO;
import com.aseo360.aseo360.dto.TrasladoInventarioDTO;
import com.aseo360.aseo360.dto.IngresoMercaderiaDTO;
import com.aseo360.aseo360.dto.LineaIngresoDTO;
import com.aseo360.aseo360.dto.SalidaMercaderiaDTO;
import com.aseo360.aseo360.dto.LineaSalidaDTO;
import com.aseo360.aseo360.modelo.*;
import com.aseo360.aseo360.repositorio.*;
import com.aseo360.aseo360.servicio.interfaz.IInventarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.ArrayList;

@Service
public class InventarioServicio implements IInventarioServicio {

    private final IInventarioRepositorio inventarioRepositorio;
    private final IInventarioProductoRepositorio inventarioProductoRepositorio;
    private final ISedeRepositorio sedeRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final ITrasladoInventarioRepositorio trasladoInventarioRepositorio;
    private final IKardexInventarioRepositorio kardexInventarioRepositorio;
    private final IProveedorRepositorio proveedorRepositorio;

    @Autowired
    public InventarioServicio(IInventarioRepositorio inventarioRepositorio,
            IInventarioProductoRepositorio inventarioProductoRepositorio,
            ISedeRepositorio sedeRepositorio,
            IProductoRepositorio productoRepositorio,
            ITrasladoInventarioRepositorio trasladoInventarioRepositorio,
            IKardexInventarioRepositorio kardexInventarioRepositorio,
            IProveedorRepositorio proveedorRepositorio) {
        this.inventarioRepositorio = inventarioRepositorio;
        this.inventarioProductoRepositorio = inventarioProductoRepositorio;
        this.sedeRepositorio = sedeRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.trasladoInventarioRepositorio = trasladoInventarioRepositorio;
        this.kardexInventarioRepositorio = kardexInventarioRepositorio;
        this.proveedorRepositorio = proveedorRepositorio;
    }

    @Override
    @Transactional
    public Inventario crearInventario(InventarioRegistroDTO inventarioRegistroDTO) throws Exception {
        Sede sede = sedeRepositorio.findById(inventarioRegistroDTO.getIdSede())
                .orElseThrow(() -> new Exception("Sede no encontrada"));

        Inventario inventario = new Inventario();
        inventario.setNombre(inventarioRegistroDTO.getNombre());
        try {
            inventario.setTipo(TipoInventario.valueOf(inventarioRegistroDTO.getTipo()));
        } catch (IllegalArgumentException e) {
            throw new Exception("Tipo de inventario invalido. Use PRINCIPAL o TIENDA");
        }
        inventario.setSede(sede);

        return inventarioRepositorio.save(inventario);
    }

    @Override
    public List<Inventario> listarInventariosPorSede(Long idSede) {
        Sede sede = sedeRepositorio.findById(idSede).orElse(null);
        if (sede == null)
            return List.of();
        return inventarioRepositorio.findBySede(sede);
    }

    @Override
    public List<Inventario> listarInventarios() {
        return inventarioRepositorio.findAll();
    }

    @Override
    public Inventario obtenerInventarioPorId(Long id) throws Exception {
        return inventarioRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Inventario no encontrado con ID: " + id));
    }

    @Override
    public List<InventarioProducto> listarProductosPorInventario(Long idInventario) throws Exception {
        Inventario inventario = obtenerInventarioPorId(idInventario);
        return inventarioProductoRepositorio.findByInventario(inventario);
    }

    @Override
    public InventarioProducto obtenerStockProducto(Long idInventario, String idProducto) throws Exception {
        Inventario inventario = obtenerInventarioPorId(idInventario);
        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new Exception("Producto no encontrado: " + idProducto));

        return inventarioProductoRepositorio.findByInventarioAndProducto(inventario, producto)
                .orElseGet(() -> {
                    InventarioProducto ip = new InventarioProducto();
                    ip.setInventario(inventario);
                    ip.setProducto(producto);
                    ip.setStock(0);
                    return inventarioProductoRepositorio.save(ip);
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sincronizarProductosCero(Long idInventario) throws Exception {
        Inventario inv = inventarioRepositorio.findById(idInventario)
                .orElseThrow(() -> new Exception("Inventario no encontrado con ID: " + idInventario));

        List<Producto> todosLosProductos = productoRepositorio.findAll();

        for (Producto producto : todosLosProductos) {
            // Verifica si el producto ya existe en este inventario
            if (inventarioProductoRepositorio.findByInventarioAndProducto(inv, producto).isEmpty()) {
                InventarioProducto ip = new InventarioProducto();
                ip.setInventario(inv);
                ip.setProducto(producto);
                ip.setStock(0);
                inventarioProductoRepositorio.save(ip);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarStock(Long idInventario, String idProducto, Integer cantidad, String tipoMovimiento,
            String referencia, String usuario) throws Exception {
        InventarioProducto ip = obtenerStockProducto(idInventario, idProducto);

        int stockAnterior = ip.getStock();
        int nuevoStock = stockAnterior;

        // Lógica simple por tipo de movimiento, se puede expandir
        if (tipoMovimiento.contains("ENTRADA") || tipoMovimiento.equals("DEVOLUCION")
                || tipoMovimiento.equals("COMPRA")) {
            nuevoStock += cantidad;
        } else if (tipoMovimiento.contains("SALIDA") || tipoMovimiento.equals("VENTA")) {
            if (stockAnterior < cantidad) {
                throw new Exception("Stock insuficiente en el inventario seleccionado para el producto: "
                        + ip.getProducto().getNombre() + " (Stock actual: " + stockAnterior + ")");
            }
            nuevoStock -= cantidad;
        }

        ip.setStock(nuevoStock);
        inventarioProductoRepositorio.save(ip);

        // Registrar en Kardex
        KardexInventario kardex = new KardexInventario();
        kardex.setInventario(ip.getInventario());
        kardex.setProducto(ip.getProducto());
        kardex.setFecha(LocalDateTime.now());
        kardex.setTipoMovimiento(tipoMovimiento);
        kardex.setCantidad(cantidad);
        kardex.setStockAnterior(stockAnterior);
        kardex.setStockActual(nuevoStock);
        kardex.setReferencia(referencia);
        kardexInventarioRepositorio.save(kardex);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> registrarIngresoMercaderia(IngresoMercaderiaDTO dto) throws Exception {
        Sede sede = sedeRepositorio.findById(dto.getIdSede())
                .orElseThrow(() -> new Exception("Sede no encontrada con ID: " + dto.getIdSede()));

        Inventario inventario = inventarioRepositorio.findBySedeAndTipo(sede, TipoInventario.PRINCIPAL)
                .orElseThrow(
                        () -> new Exception("Inventario principal no encontrado para la Sede: " + sede.getNombre()));

        List<String> alertasDePrecio = new ArrayList<>();

        for (LineaIngresoDTO linea : dto.getLineas()) {
            Producto producto = productoRepositorio.findById(linea.getIdProducto())
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + linea.getIdProducto()));

            Proveedor proveedor = proveedorRepositorio.findById(linea.getIdProveedor())
                    .orElseThrow(() -> new Exception("Proveedor no encontrado con RUC: " + linea.getIdProveedor()));

            // Obtener stock actual o crearlo
            InventarioProducto inventarioProducto = inventarioProductoRepositorio
                    .findByInventarioAndProducto(inventario, producto)
                    .orElseGet(() -> {
                        InventarioProducto nuevo = new InventarioProducto();
                        nuevo.setInventario(inventario);
                        nuevo.setProducto(producto);
                        nuevo.setStock(0);
                        return nuevo;
                    });

            int stockAnterior = inventarioProducto.getStock() != null ? inventarioProducto.getStock() : 0;
            int stockActual = stockAnterior + linea.getCantidad();

            inventarioProducto.setStock(stockActual);
            inventarioProductoRepositorio.save(inventarioProducto);

            // Si el precio de costo ha variado drásticamente se guardará en Producto
            producto.setPrecioCompra(linea.getPrecioCosto());
            productoRepositorio.save(producto);

            // Validar utilidades para enviar alertas
            double costo = linea.getPrecioCosto() != null ? linea.getPrecioCosto().doubleValue() : 0.0;
            double venta = producto.getPrecioVenta() != null ? producto.getPrecioVenta().doubleValue() : 0.0;

            if (venta > 0) {
                if (costo >= venta) {
                    alertasDePrecio.add("⛔ " + producto.getNombre()
                            + ": Estás comprando a S/ " + String.format("%.2f", costo)
                            + " pero vendes a S/ " + String.format("%.2f", venta)
                            + ". Vas a perder dinero con cada venta. Sube el precio de venta.");
                } else {
                    double margen = ((venta - costo) / venta) * 100;
                    if (margen <= 15.0) {
                        alertasDePrecio.add("⚠️ " + producto.getNombre()
                                + ": Tu ganancia es solo del " + String.format("%.1f", margen)
                                + "% (compras a S/ " + String.format("%.2f", costo)
                                + ", vendes a S/ " + String.format("%.2f", venta)
                                + "). Considera subir el precio de venta.");
                    }
                }
            }

            // Registrar movimiento en Kardex
            KardexInventario kardex = new KardexInventario();
            kardex.setInventario(inventario);
            kardex.setProducto(producto);
            kardex.setProveedor(proveedor);
            kardex.setTipoMovimiento("INGRESO");
            kardex.setCantidad(linea.getCantidad());
            kardex.setStockAnterior(stockAnterior);
            kardex.setStockActual(stockActual);

            kardex.setFecha(LocalDateTime.now());

            String observacion = dto.getObservacion() != null && !dto.getObservacion().isEmpty() ? dto.getObservacion()
                    : "Ingreso de Almacén";
            kardex.setReferencia(dto.getTipoIngreso() + " - " + observacion);

            kardexInventarioRepositorio.save(kardex);
        }

        return alertasDePrecio;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarSalidaMercaderia(SalidaMercaderiaDTO dto) throws Exception {
        Sede sede = sedeRepositorio.findById(dto.getIdSede())
                .orElseThrow(() -> new Exception("Sede no encontrada con ID: " + dto.getIdSede()));

        Inventario inventario = inventarioRepositorio.findBySedeAndTipo(sede, TipoInventario.PRINCIPAL)
                .orElseThrow(
                        () -> new Exception("Inventario principal no encontrado para la Sede: " + sede.getNombre()));

        for (LineaSalidaDTO linea : dto.getLineas()) {
            Producto producto = productoRepositorio.findById(linea.getIdProducto())
                    .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + linea.getIdProducto()));

            InventarioProducto inventarioProducto = inventarioProductoRepositorio
                    .findByInventarioAndProducto(inventario, producto)
                    .orElseThrow(() -> new Exception("El producto " + producto.getNombre()
                            + " no está registrado en el inventario " + inventario.getNombre()));

            int stockAnterior = inventarioProducto.getStock() != null ? inventarioProducto.getStock() : 0;
            int cantidadMovimiento = linea.getCantidad();
            int stockActual = stockAnterior - cantidadMovimiento;

            if (stockActual < 0) {
                throw new Exception("Stock insuficiente para: " + producto.getNombre() + ". Stock actual: "
                        + stockAnterior + ", Cantidad requerida: " + cantidadMovimiento);
            }

            // Descontar inventario
            inventarioProducto.setStock(stockActual);
            inventarioProductoRepositorio.save(inventarioProducto);

            // Registrar movimiento en Kardex
            KardexInventario kardex = new KardexInventario();
            kardex.setInventario(inventario);
            kardex.setProducto(producto);
            kardex.setTipoMovimiento("SALIDA");
            kardex.setCantidad(cantidadMovimiento);
            kardex.setStockAnterior(stockAnterior);
            kardex.setStockActual(stockActual);

            kardex.setFecha(LocalDateTime.now());

            String observacion = dto.getObservacion() != null && !dto.getObservacion().isEmpty() ? dto.getObservacion()
                    : "Salida de Almacén";
            kardex.setReferencia(dto.getMotivo() + " - " + observacion);

            kardexInventarioRepositorio.save(kardex);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void procesarTraslado(TrasladoInventarioDTO dto) throws Exception {
        Long idOrigen = dto.getIdOrigen();
        Long idDestino = dto.getIdDestino();
        String idProducto = dto.getIdProducto();
        Integer cantidad = dto.getCantidad();
        String usuario = dto.getUsuario();
        String motivo = dto.getMotivo();

        if (idOrigen.equals(idDestino)) {
            throw new Exception("El inventario de origen y destino no pueden ser iguales");
        }

        Inventario origen = obtenerInventarioPorId(idOrigen);
        Inventario destino = obtenerInventarioPorId(idDestino);
        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new Exception("Producto no encontrado"));

        // Validar que hay stock disponible antes de registrar la solicitud En Proceso
        // (opcional pero bueno)
        InventarioProducto ipOrigen = obtenerStockProducto(idOrigen, idProducto);
        if (ipOrigen.getStock() < cantidad) {
            throw new Exception("Stock insuficiente en el inventario origen para solicitar traslado");
        }

        TrasladoInventario traslado = new TrasladoInventario();
        traslado.setInventarioOrigen(origen);
        traslado.setInventarioDestino(destino);
        traslado.setProducto(producto);
        traslado.setCantidad(cantidad);
        traslado.setFecha(LocalDateTime.now());
        traslado.setUsuario(usuario);
        traslado.setMotivo(motivo);
        traslado.setEstado("EN_PROCESO");

        trasladoInventarioRepositorio.save(traslado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cambiarEstadoTraslado(Long idTraslado, String nuevoEstado, String usuario) throws Exception {
        TrasladoInventario traslado = trasladoInventarioRepositorio.findById(idTraslado)
                .orElseThrow(() -> new Exception("Traslado no encontrado"));

        if (!traslado.getEstado().equals("EN_PROCESO")) {
            throw new Exception("El traslado ya fue procesado (" + traslado.getEstado() + ")");
        }

        if (nuevoEstado.equals("FINALIZADO")) {
            // 1. Descontar de Origen
            actualizarStock(
                    traslado.getInventarioOrigen().getIdInventario(),
                    traslado.getProducto().getIdProducto(),
                    traslado.getCantidad(),
                    "TRASLADO_SALIDA",
                    "Hacia Inv #" + traslado.getInventarioDestino().getIdInventario() + " | Solicitud #"
                            + traslado.getIdTraslado(),
                    usuario);

            // 2. Aumentar en Destino
            actualizarStock(
                    traslado.getInventarioDestino().getIdInventario(),
                    traslado.getProducto().getIdProducto(),
                    traslado.getCantidad(),
                    "TRASLADO_ENTRADA",
                    "Desde Inv #" + traslado.getInventarioOrigen().getIdInventario() + " | Solicitud #"
                            + traslado.getIdTraslado(),
                    usuario);
        } else if (!nuevoEstado.equals("CANCELADO")) {
            throw new Exception("Estado no válido: " + nuevoEstado);
        }

        traslado.setEstado(nuevoEstado);
        traslado.setMotivo(traslado.getMotivo() + " | Resolvió: " + usuario);
        trasladoInventarioRepositorio.save(traslado);
    }

    @Override
    public List<TrasladoInventario> listarTraslados() {
        return trasladoInventarioRepositorio.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inicializarStockProducto(Producto producto, Long idInventarioSeleccionado,
            Integer cantidadInicial, String idProveedor) {
        List<Inventario> inventarios = inventarioRepositorio.findAll();

        Proveedor proveedor = null;
        if (idProveedor != null && !idProveedor.isEmpty()) {
            proveedor = proveedorRepositorio.findById(idProveedor).orElse(null);
        }

        for (Inventario inv : inventarios) {
            // Solo creamos el link Producto-Inventario si no existiera previamente.
            if (inventarioProductoRepositorio.findByInventarioAndProducto(inv, producto).isEmpty()) {
                InventarioProducto ip = new InventarioProducto();
                ip.setInventario(inv);
                ip.setProducto(producto);

                // Si este inventario corresponde al elegido por el usuario,
                // colocamos la Cantidad. En los demas, 0.
                boolean esInventarioCorrecto = inv.getIdInventario().equals(idInventarioSeleccionado);

                if (esInventarioCorrecto) {
                    ip.setStock(cantidadInicial);

                    // Solo si la cantidad inicial es mayor a 0, registrar en el kardex el ingreso
                    if (cantidadInicial != null && cantidadInicial > 0) {
                        KardexInventario kardex = new KardexInventario();
                        kardex.setInventario(inv);
                        kardex.setProducto(producto);
                        kardex.setProveedor(proveedor);
                        kardex.setFecha(LocalDateTime.now());
                        kardex.setTipoMovimiento("ENTRADA_INICIAL");
                        kardex.setCantidad(cantidadInicial);
                        kardex.setStockAnterior(0);
                        kardex.setStockActual(cantidadInicial);
                        kardex.setReferencia("Ingreso Inicial al Catálogo");
                        kardexInventarioRepositorio.save(kardex);
                    }
                } else {
                    ip.setStock(0);
                }

                inventarioProductoRepositorio.save(ip);
            }
        }
    }

    @Override
    public Page<KardexInventario> listarKardex(Long idInventario, Pageable pageable) {
        Inventario inventario = inventarioRepositorio.findById(idInventario).orElse(null);
        if (inventario == null)
            return Page.empty();
        return kardexInventarioRepositorio.findByInventario(inventario, pageable);
    }

    @Override
    public Page<KardexInventario> listarKardexGlobal(Pageable pageable) {
        return kardexInventarioRepositorio.findAll(pageable);
    }
}
