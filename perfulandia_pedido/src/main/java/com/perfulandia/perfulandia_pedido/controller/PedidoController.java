package com.perfulandia.perfulandia_pedido.controller;

import com.perfulandia.perfulandia_pedido.model.PagoRequest;
import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.Producto;
import com.perfulandia.perfulandia_pedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido pedido) {
        int usuarioId = Integer.parseInt(pedido.getCliente());
        if (!pedidoService.usuarioExiste(usuarioId)) {
            return ResponseEntity.badRequest().body("El usuario no existe, no se puede crear el pedido.");
        }

        // Validar stock antes de crear el pedido
        Long productoId = Long.parseLong(pedido.getProducto());
        Integer stock = pedidoService.consultarStockProducto(productoId);
        if (stock == null) {
            return ResponseEntity.status(404).body("Producto no encontrado en inventario");
        }
        if (stock < pedido.getCantidad()) {
            return ResponseEntity.status(409).body("Stock insuficiente. Disponible: " + stock);
        }

        Pedido nuevoPedido = pedidoService.crearPedido(pedido);

        // Registrar el pago
        PagoRequest pago = new PagoRequest();
        pago.setPedidoId((long) nuevoPedido.getId());
        pago.setMonto(nuevoPedido.getTotal());
        pago.setMetodo("EFECTIVO");
        pago.setEstado("PENDIENTE");
        pedidoService.registrarPago(pago);

        return ResponseEntity.ok(nuevoPedido);
    }

    @GetMapping("/{id}")
    public Pedido buscarPedido(@PathVariable int id) {
        return pedidoService.buscarPedido(id);
    }

    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable int id, @RequestBody Pedido pedido) {
        return pedidoService.actualizarPedido(id, pedido);
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable int id) {
        pedidoService.eliminarPedido(id);
    }

    @PatchMapping("/{id}")
    public Pedido actualizarParcialmentePedido(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        Pedido pedido = pedidoService.buscarPedido(id);
        if (pedido == null) {
            return null;
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Pedido.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pedido, value);
            }
        });
        return pedidoService.crearPedido(pedido);
    }

    // --- Nuevo endpoint para obtener productos desde Pedido ---
    @GetMapping("/productos-externos")
    public List<Producto> obtenerProductosExternos() {
        return pedidoService.obtenerProductos();
    }

    // Endpoint para consultar stock de un producto desde Pedido
    @GetMapping("/stock-producto/{productoId}")
    public Integer consultarStockProducto(@PathVariable Long productoId) {
        return pedidoService.consultarStockProducto(productoId);
    }

    @GetMapping("/verificar-stock/{productoId}/{cantidad}")
    public ResponseEntity<String> verificarStock(
            @PathVariable Long productoId,
            @PathVariable int cantidad) {
        Integer stock = pedidoService.consultarStockProducto(productoId);
        if (stock == null) {
            return ResponseEntity.status(404).body("Producto no encontrado en inventario");
        }
        if (stock >= cantidad) {
            return ResponseEntity.ok("Stock suficiente: " + stock);
        } else {
            return ResponseEntity.status(409).body("Stock insuficiente. Disponible: " + stock);
        }
    }
}
