package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.PedidoEstadoDTO;
import com.aseo360.aseo360.dto.PedidoRegistroDTO;
import com.aseo360.aseo360.dto.ProductoCarritoDTO;
import com.aseo360.aseo360.modelo.*;
import com.aseo360.aseo360.repositorio.*;
import com.aseo360.aseo360.servicio.interfaz.IInventarioServicio;
import com.aseo360.aseo360.servicio.interfaz.IPedidoServicio;
import com.aseo360.aseo360.servicio.interfaz.IProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class PedidoServicio implements IPedidoServicio {

    private final IPedidoRepositorio pedidoRepositorio;
    private final IDetallePedidoRepositorio detallePedidoRepositorio;
    private final IClienteRepositorio clienteRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final IProductoServicio productoServicio;
    private final IInventarioServicio inventarioServicio;
    private final IInventarioRepositorio inventarioRepositorio;
    private final ISedeRepositorio sedeRepositorio;

    @Autowired
    public PedidoServicio(IPedidoRepositorio pedidoRepositorio, IDetallePedidoRepositorio detallePedidoRepositorio,
            IClienteRepositorio clienteRepositorio, IProductoRepositorio productoRepositorio,
            IProductoServicio productoServicio, IInventarioServicio inventarioServicio,
            IInventarioRepositorio inventarioRepositorio, ISedeRepositorio sedeRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.detallePedidoRepositorio = detallePedidoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.productoServicio = productoServicio;
        this.inventarioServicio = inventarioServicio;
        this.inventarioRepositorio = inventarioRepositorio;
        this.sedeRepositorio = sedeRepositorio;
    }

    @Override
    public Page<Pedido> listarPedidos(Pageable pageable) {
        return this.pedidoRepositorio.findAll(pageable);
    }

    @Override
    public List<DetallePedido> listarDetallesPorPedioId(Long id) throws Exception {
        Pedido pedido = this.pedidoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("No se encontro el pedido."));
        return this.detallePedidoRepositorio.findAllByPedido(pedido);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pedido registrarPedido(PedidoRegistroDTO pedidoRegistroDTO) throws Exception {
        Pedido pedido = new Pedido();
        LocalDate hoy = LocalDate.now();
        Cliente cliente = clienteRepositorio.findById(pedidoRegistroDTO.getIdCliente())
                .orElseThrow(() -> new Exception("Cliente no encontrado"));

        pedido.setCliente(cliente);
        pedido.setFecha(hoy);
        pedido.setEstado("PENDIENTE");
        pedido.setFormaPago(pedidoRegistroDTO.getFormaPago());
        pedido.setTotalPedido(BigDecimal.ZERO); // Se inicializa en cero

        Pedido pedidoGuardado = this.pedidoRepositorio.save(pedido);

        BigDecimal pedidoTotalAcumulado = BigDecimal.ZERO;

        // TODO: Definir logica de seleccion de inventario para pedidos online.
        // Por ahora usamos el inventario PRINCIPAL de la primera sede encontrada o de
        // la sede 1.
        // Esto es temporal para mantener compatibilidad.
        Long idSedeDefecto = 1L;
        Sede sede = sedeRepositorio.findById(idSedeDefecto)
                .orElseThrow(() -> new Exception("Sede por defecto no encontrada"));
        Inventario inventario = inventarioRepositorio.findBySedeAndTipo(sede, TipoInventario.PRINCIPAL)
                .orElseThrow(() -> new Exception("Inventario Principal no encontrado en sede por defecto"));

        for (ProductoCarritoDTO item : pedidoRegistroDTO.getProductoList()) {
            Producto producto = this.productoRepositorio.findById(item.getIdProducto())
                    .orElseThrow(() -> new Exception("Producto no encontrado : " + item.getIdProducto()));

            // Actualizar stock (descontar)
            inventarioServicio.actualizarStock(
                    inventario.getIdInventario(),
                    producto.getIdProducto(),
                    item.getCantidad(),
                    "PEDIDO",
                    "Pedido #" + pedidoGuardado.getIdPedido(),
                    "Sistema");

            // Calculo logica minorista o mayorista
            BigDecimal precioAplicado = calcularPrecio(item.getCantidad(), producto);

            // Calculo subtotal
            BigDecimal subTotal = precioAplicado.multiply(new BigDecimal(item.getCantidad()));

            // Crear detalle
            DetallePedido detallePedido = new DetallePedido();

            detallePedido.setPedido(pedidoGuardado);
            detallePedido.setCantidad(item.getCantidad());
            detallePedido.setProducto(producto);
            detallePedido.setPrecioUnitario(precioAplicado);
            detallePedido.setSubTotal(subTotal);

            this.detallePedidoRepositorio.save(detallePedido);

            pedidoTotalAcumulado = pedidoTotalAcumulado.add(subTotal);
        }
        pedidoGuardado.setTotalPedido(pedidoTotalAcumulado);

        return this.pedidoRepositorio.save(pedidoGuardado);
    }

    @Override
    public Pedido buscarPedidoPorId(Long id) throws Exception {
        return this.pedidoRepositorio.findById(id).orElseThrow(() -> new Exception("Error: pedido no encontrado"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pedido anularPedido(Long id) throws Exception {
        Pedido pedido = this.pedidoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Error: pedido no encontrado"));
        if ("ANULADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new Exception("Error: ¡El pedido ya se encuentra anulado!");
        }
        pedido.setEstado("ANULADO");
        List<DetallePedido> detalles = this.detallePedidoRepositorio.findAllByPedido(pedido);

        // Recuperar inventario usado (asumiendo el por defecto por ahora, idealmente
        // guardar inventario en Pedido)
        Long idSedeDefecto = 1L;
        Sede sede = sedeRepositorio.findById(idSedeDefecto)
                .orElseThrow(() -> new Exception("Sede por defecto no encontrada"));
        Inventario inventario = inventarioRepositorio.findBySedeAndTipo(sede, TipoInventario.PRINCIPAL)
                .orElseThrow(() -> new Exception("Inventario Principal no encontrado en sede por defecto"));

        for (DetallePedido detalle : detalles) {
            inventarioServicio.actualizarStock(
                    inventario.getIdInventario(),
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad(),
                    "DEVOLUCION_PEDIDO",
                    "Anulacion Pedido #" + pedido.getIdPedido(),
                    "Sistema");
        }
        return this.pedidoRepositorio.save(pedido);
    }

    @Override
    public Pedido modificarEstadoPedido(PedidoEstadoDTO pedidoEstadoDTO) throws Exception {
        Pedido pedido = this.pedidoRepositorio.findById(pedidoEstadoDTO.getIdPedido())
                .orElseThrow(() -> new Exception("No se encontro el pedido"));
        if ("ANULADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new Exception("El pedido se encuentra anulado, no puede cambiar el estado!");
        }
        pedido.setEstado(pedidoEstadoDTO.getEstado());
        return this.pedidoRepositorio.save(pedido);
    }

    @Override
    public Page<Pedido> listarPedidoPorCliente(Cliente cliente, Pageable pageable) {
        return this.pedidoRepositorio.findAllByCliente(cliente, pageable);
    }

    private BigDecimal calcularPrecio(Integer cantidad, Producto producto) {
        return (cantidad > 3) ? producto.getPrecioPorMayor() : producto.getPrecioVenta();
    }
}
