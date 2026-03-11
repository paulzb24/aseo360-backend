package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.ProductoRegistroDTO;
import com.aseo360.aseo360.dto.ProductoResponseDTO;
import com.aseo360.aseo360.dto.StockDetalleDTO;
import com.aseo360.aseo360.modelo.*;
import com.aseo360.aseo360.repositorio.*;
import com.aseo360.aseo360.servicio.interfaz.IInventarioServicio;
import com.aseo360.aseo360.servicio.interfaz.IProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductoServicio implements IProductoServicio {
        private final IProductoRepositorio productoRepositorio;
        private final IAromaRepositorio aromaRepositorio;
        private final ISedeRepositorio sedeRepositorio;
        private final ICategoriaProductoRepositorio categoriaProductoRepositorio;
        private final IInventarioServicio inventarioServicio;
        private final IInventarioProductoRepositorio inventarioProductoRepositorio;
        private final IKardexInventarioRepositorio kardexInventarioRepositorio;

        @Autowired
        public ProductoServicio(IProductoRepositorio productoRepositorio, IAromaRepositorio aromaRepositorio,
                        ISedeRepositorio sedeRepositorio,
                        ICategoriaProductoRepositorio categoriaProductoRepositorio,
                        IInventarioServicio inventarioServicio,
                        IInventarioProductoRepositorio inventarioProductoRepositorio,
                        IKardexInventarioRepositorio kardexInventarioRepositorio) {
                this.productoRepositorio = productoRepositorio;
                this.aromaRepositorio = aromaRepositorio;
                this.sedeRepositorio = sedeRepositorio;
                this.categoriaProductoRepositorio = categoriaProductoRepositorio;
                this.inventarioServicio = inventarioServicio;
                this.inventarioProductoRepositorio = inventarioProductoRepositorio;
                this.kardexInventarioRepositorio = kardexInventarioRepositorio;
        }

        @Override
        public Page<ProductoResponseDTO> listarProductos(Pageable pageable) throws Exception {
                Page<Producto> productosPage = this.productoRepositorio.findAll(pageable);
                List<Producto> productos = productosPage.getContent();

                if (productos.isEmpty()) {
                        return new PageImpl<>(Collections.emptyList(), pageable, productosPage.getTotalElements());
                }

                // Batch Fetching: Traer stock de todos los productos en una sola consulta
                List<InventarioProducto> inventarios = inventarioProductoRepositorio.findByProductoIn(productos);

                // Agrupar stock por producto
                Map<String, List<InventarioProducto>> stockPorProducto = inventarios.stream()
                                .collect(Collectors.groupingBy(ip -> ip.getProducto().getIdProducto()));

                List<ProductoResponseDTO> productoResponseDTOS = productos.stream().map(
                                producto -> {
                                        List<InventarioProducto> stockProducto = stockPorProducto.getOrDefault(
                                                        producto.getIdProducto(),
                                                        Collections.emptyList());

                                        List<StockDetalleDTO> stockDetalle = stockProducto.stream()
                                                        .map(ip -> new StockDetalleDTO(
                                                                        ip.getInventario().getIdInventario(),
                                                                        ip.getInventario().getNombre() + " ("
                                                                                        + ip.getInventario().getTipo()
                                                                                                        .name()
                                                                                        + ")",
                                                                        ip.getInventario().getSede().getIdSede(),
                                                                        ip.getInventario().getSede().getNombre(),
                                                                        ip.getStock()))
                                                        .toList();

                                        return new ProductoResponseDTO(
                                                        producto.getIdProducto(),
                                                        producto.getImagen(),
                                                        producto.getNombre(),
                                                        producto.getDescripcion(),
                                                        producto.getPrecioCompra(),
                                                        producto.getPrecioVenta(),
                                                        producto.getPrecioPorMayor(),
                                                        producto.getCategoriaProducto() != null
                                                                        ? producto.getCategoriaProducto()
                                                                                        .getIdCategoria()
                                                                        : null,
                                                        producto.getCategoriaProducto() != null
                                                                        ? producto.getCategoriaProducto().getNombre()
                                                                        : null,
                                                        producto.getAroma() != null ? producto.getAroma().getIdAroma()
                                                                        : null,
                                                        producto.getAroma() != null ? producto.getAroma().getNombre()
                                                                        : null,
                                                        producto.getPeso(),
                                                        producto.getPresentacion(),
                                                        producto.getFechaRegistro(),
                                                        producto.getEstado(),
                                                        stockDetalle);
                                })
                                .toList();

                return new PageImpl<>(productoResponseDTOS, pageable, productosPage.getTotalElements());
        }

        @Override
        public Page<ProductoResponseDTO> listarProductosDisponibles(Pageable pageable) throws Exception {
                Page<Producto> productosPage = this.productoRepositorio.findAllByEstado("DISPONIBLE", pageable);
                List<Producto> productos = productosPage.getContent();

                if (productos.isEmpty()) {
                        return new PageImpl<>(Collections.emptyList(), pageable, productosPage.getTotalElements());
                }

                // Batch Fetching
                List<InventarioProducto> inventarios = inventarioProductoRepositorio.findByProductoIn(productos);
                Map<String, List<InventarioProducto>> stockPorProducto = inventarios.stream()
                                .collect(Collectors.groupingBy(ip -> ip.getProducto().getIdProducto()));

                List<ProductoResponseDTO> productosDisponibles = productos.stream().map(
                                producto -> {
                                        List<InventarioProducto> stockProducto = stockPorProducto.getOrDefault(
                                                        producto.getIdProducto(),
                                                        Collections.emptyList());

                                        List<StockDetalleDTO> stockDetalle = stockProducto.stream()
                                                        .map(ip -> new StockDetalleDTO(
                                                                        ip.getInventario().getIdInventario(),
                                                                        ip.getInventario().getNombre() + " ("
                                                                                        + ip.getInventario().getTipo()
                                                                                                        .name()
                                                                                        + ")",
                                                                        ip.getInventario().getSede().getIdSede(),
                                                                        ip.getInventario().getSede().getNombre(),
                                                                        ip.getStock()))
                                                        .toList();

                                        return new ProductoResponseDTO(
                                                        producto.getIdProducto(),
                                                        producto.getImagen(),
                                                        producto.getNombre(),
                                                        producto.getDescripcion(),
                                                        producto.getPrecioCompra(),
                                                        producto.getPrecioVenta(),
                                                        producto.getPrecioPorMayor(),
                                                        producto.getCategoriaProducto() != null
                                                                        ? producto.getCategoriaProducto()
                                                                                        .getIdCategoria()
                                                                        : null,
                                                        producto.getCategoriaProducto() != null
                                                                        ? producto.getCategoriaProducto().getNombre()
                                                                        : null,
                                                        producto.getAroma() != null ? producto.getAroma().getIdAroma()
                                                                        : null,
                                                        producto.getAroma() != null ? producto.getAroma().getNombre()
                                                                        : null,
                                                        producto.getPeso(),
                                                        producto.getPresentacion(),
                                                        producto.getFechaRegistro(),
                                                        producto.getEstado(),
                                                        stockDetalle);
                                })
                                .toList();

                return new PageImpl<>(productosDisponibles, pageable, productosPage.getTotalElements());
        }

        @Override
        public Producto registrarProducto(ProductoRegistroDTO productoRegistroDTO) throws Exception {
                LocalDate hoy = LocalDate.now();
                Producto producto = new Producto();

                Aroma aroma = null;
                if (productoRegistroDTO.getIdAroma() != null) {
                        aroma = this.aromaRepositorio.findById(productoRegistroDTO.getIdAroma())
                                        .orElseThrow(() -> new Exception(
                                                        "Aroma no encontrado con id: "
                                                                        + productoRegistroDTO.getIdAroma()));
                }

                CategoriaProducto categoriaProducto = this.categoriaProductoRepositorio
                                .findById(productoRegistroDTO.getIdCategoriaProducto()).orElseThrow(() -> new Exception(
                                                "Categoria no encontrada con id: "
                                                                + productoRegistroDTO.getIdCategoriaProducto()));

                // Completamos los datos
                producto.setIdProducto(productoRegistroDTO.getIdProducto());
                producto.setCategoriaProducto(categoriaProducto);
                producto.setAroma(aroma);
                producto.setNombre(productoRegistroDTO.getNombre());
                producto.setDescripcion(productoRegistroDTO.getDescripcion());
                producto.setImagen(productoRegistroDTO.getImagen());
                producto.setPrecioCompra(productoRegistroDTO.getPrecioCompra());
                producto.setPrecioVenta(productoRegistroDTO.getPrecioVenta());
                producto.setPrecioPorMayor(productoRegistroDTO.getPrecioMayor());
                producto.setPeso(productoRegistroDTO.getPeso());
                producto.setPresentacion(productoRegistroDTO.getPresentacion());
                producto.setFechaRegistro(hoy);
                producto.setEstado(productoRegistroDTO.getEstado());

                // Guardamos en la bd
                Producto guardado = this.productoRepositorio.save(producto);

                // Inicializar stock en 0 en todos los inventarios, salvo en el específico donde
                // se depositará la cantidad inicial
                inventarioServicio.inicializarStockProducto(
                                guardado,
                                productoRegistroDTO.getIdInventario(),
                                productoRegistroDTO.getCantidad(),
                                productoRegistroDTO.getIdProveedor());

                return guardado;
        }

        @Override
        public Producto modificarProducto(ProductoRegistroDTO productoRegistroDTO) throws Exception {
                Producto producto = this.productoRepositorio.findById(productoRegistroDTO.getIdProducto()).orElseThrow(
                                () -> new Exception("Producto no encontrado con id: "
                                                + productoRegistroDTO.getIdProducto()));

                Aroma aroma = null;
                if (productoRegistroDTO.getIdAroma() != null) {
                        aroma = this.aromaRepositorio.findById(productoRegistroDTO.getIdAroma())
                                        .orElseThrow(() -> new Exception(
                                                        "Aroma no encontrado con id: "
                                                                        + productoRegistroDTO.getIdAroma()));
                }

                CategoriaProducto categoriaProducto = this.categoriaProductoRepositorio
                                .findById(productoRegistroDTO.getIdCategoriaProducto()).orElseThrow(() -> new Exception(
                                                "Categoria no encontrada con id: "
                                                                + productoRegistroDTO.getIdCategoriaProducto()));

                // Completamos los datos
                producto.setCategoriaProducto(categoriaProducto);
                producto.setAroma(aroma);
                producto.setNombre(productoRegistroDTO.getNombre());
                producto.setDescripcion(productoRegistroDTO.getDescripcion());
                producto.setImagen(productoRegistroDTO.getImagen());
                producto.setPrecioCompra(productoRegistroDTO.getPrecioCompra());
                producto.setPrecioVenta(productoRegistroDTO.getPrecioVenta());
                producto.setPrecioPorMayor(productoRegistroDTO.getPrecioMayor());
                producto.setPeso(productoRegistroDTO.getPeso());
                producto.setPresentacion(productoRegistroDTO.getPresentacion());
                producto.setEstado(productoRegistroDTO.getEstado());

                return this.productoRepositorio.save(producto);
        }

        @Override
        public Producto buscarPorId(String id) throws Exception {
                return this.productoRepositorio.findById(id)
                                .orElseThrow(() -> new Exception("Error: producto no encontrado"));
        }

        @Override
        @org.springframework.transaction.annotation.Transactional
        public void eliminarPorId(String id) throws Exception {
                Producto producto = this.productoRepositorio.findById(id)
                                .orElseThrow(() -> new Exception("Producto no encontrado"));

                // Borrar Kardex (Auditoría) del producto
                java.util.List<KardexInventario> kardex = this.kardexInventarioRepositorio.findByProducto(producto);
                if (!kardex.isEmpty()) {
                        this.kardexInventarioRepositorio.deleteAll(kardex);
                }

                // Borrar Stock (InventariosProducto) del producto
                java.util.List<InventarioProducto> inventarios = this.inventarioProductoRepositorio
                                .findByProducto(producto);
                if (!inventarios.isEmpty()) {
                        this.inventarioProductoRepositorio.deleteAll(inventarios);
                }

                // Finalmente borrar el producto
                this.productoRepositorio.delete(producto);
        }

        @Override
        public Producto aumentarStock(String id, Integer cantidad) throws Exception {
                // Metodo mantenido por compatibilidad pero deberia ser reemplazado por
                // inventarioServicio.actualizarStock
                return this.productoRepositorio.findById(id).orElseThrow();
        }
}
