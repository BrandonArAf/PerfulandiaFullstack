package com.perfulandia.perfulandia_pedido.controller;

import com.perfulandia.perfulandia_pedido.model.PagoRequest;
import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.PedidoModel;
import com.perfulandia.perfulandia_pedido.model.Producto;
import com.perfulandia.perfulandia_pedido.service.PedidoHateoasService;
import com.perfulandia.perfulandia_pedido.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;

/**
 * Controlador REST para la gestión de pedidos
 * 
 * Este controlador proporciona endpoints para crear, consultar, actualizar y eliminar
 * pedidos en el sistema Perfulandia, incluyendo validaciones de stock y usuario.
 * Implementa HATEOAS para proporcionar navegabilidad en la API.
 */
@RestController
@RequestMapping("/api/v1/pedido")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos en Perfulandia")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoHateoasService pedidoHateoasService;

    @Operation(
        summary = "Listar todos los pedidos",
        description = "Obtiene una lista completa de todos los pedidos registrados en el sistema con enlaces HATEOAS"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de pedidos obtenida exitosamente con enlaces navegables",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoModel.class)
            )
        )
    })
    @GetMapping
    public CollectionModel<PedidoModel> listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        return pedidoHateoasService.toCollectionModel(pedidos);
    }

    @Operation(
        summary = "Crear un nuevo pedido",
        description = "Crea un nuevo pedido validando la existencia del usuario y el stock disponible del producto"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido creado exitosamente con enlaces HATEOAS",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "El usuario no existe",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "El usuario no existe, no se puede crear el pedido.")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado en inventario",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "Producto no encontrado en inventario")
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Stock insuficiente",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "Stock insuficiente. Disponible: 5")
            )
        )
    })
    @PostMapping
    public ResponseEntity<?> crearPedido(
            @Parameter(description = "Datos del pedido a crear", required = true)
            @RequestBody Pedido pedido) {
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

        // Retornar con enlaces HATEOAS
        PedidoModel pedidoModel = pedidoHateoasService.toModel(nuevoPedido);
        return ResponseEntity.ok(pedidoModel);
    }

    @Operation(
        summary = "Buscar pedido por ID",
        description = "Obtiene un pedido específico por su identificador único con enlaces HATEOAS"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido encontrado con enlaces navegables",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoModel> buscarPedido(
            @Parameter(description = "ID del pedido a buscar", required = true)
            @PathVariable int id) {
        Pedido pedido = pedidoService.buscarPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        PedidoModel pedidoModel = pedidoHateoasService.toModel(pedido);
        return ResponseEntity.ok(pedidoModel);
    }

    @Operation(
        summary = "Actualizar pedido completo",
        description = "Actualiza todos los campos de un pedido existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido actualizado exitosamente con enlaces HATEOAS",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado"
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoModel> actualizarPedido(
            @Parameter(description = "ID del pedido a actualizar", required = true)
            @PathVariable int id,
            @Parameter(description = "Nuevos datos del pedido", required = true)
            @RequestBody Pedido pedido) {
        Pedido pedidoActualizado = pedidoService.actualizarPedido(id, pedido);
        if (pedidoActualizado == null) {
            return ResponseEntity.notFound().build();
        }
        PedidoModel pedidoModel = pedidoHateoasService.toModel(pedidoActualizado);
        return ResponseEntity.ok(pedidoModel);
    }

    @Operation(
        summary = "Eliminar pedido",
        description = "Elimina un pedido del sistema por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Pedido eliminado exitosamente"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @Parameter(description = "ID del pedido a eliminar", required = true)
            @PathVariable int id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Actualizar pedido parcialmente",
        description = "Actualiza solo los campos especificados de un pedido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pedido actualizado parcialmente con enlaces HATEOAS",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PedidoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pedido no encontrado"
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PedidoModel> actualizarParcialmentePedido(
            @Parameter(description = "ID del pedido a actualizar", required = true)
            @PathVariable int id,
            @Parameter(description = "Campos a actualizar", required = true)
            @RequestBody Map<String, Object> updates) {
        Pedido pedido = pedidoService.buscarPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Pedido.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pedido, value);
            }
        });
        Pedido pedidoActualizado = pedidoService.crearPedido(pedido);
        PedidoModel pedidoModel = pedidoHateoasService.toModel(pedidoActualizado);
        return ResponseEntity.ok(pedidoModel);
    }

    @Operation(
        summary = "Obtener productos externos",
        description = "Obtiene la lista de productos desde el microservicio de productos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Producto.class)
            )
        )
    })
    @GetMapping("/productos-externos")
    public List<Producto> obtenerProductosExternos() {
        return pedidoService.obtenerProductos();
    }

    @Operation(
        summary = "Consultar stock de producto",
        description = "Obtiene la cantidad disponible en stock de un producto específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stock consultado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "integer")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        )
    })
    @GetMapping("/stock-producto/{productoId}")
    public Integer consultarStockProducto(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long productoId) {
        return pedidoService.consultarStockProducto(productoId);
    }

    @Operation(
        summary = "Verificar disponibilidad de stock",
        description = "Verifica si hay suficiente stock disponible para una cantidad específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Stock suficiente",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "Stock suficiente: 10")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "Producto no encontrado en inventario")
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Stock insuficiente",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "Stock insuficiente. Disponible: 3")
            )
        )
    })
    @GetMapping("/verificar-stock/{productoId}/{cantidad}")
    public ResponseEntity<String> verificarStock(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad requerida", required = true)
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
