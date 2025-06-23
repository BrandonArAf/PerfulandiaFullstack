package com.perfulandia.perfulandia_producto.controller;

import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.model.ProductoModel;
import com.perfulandia.perfulandia_producto.service.ProductoHateoasService;
import com.perfulandia.perfulandia_producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/producto")
@Tag(name = "Producto", description = "API para la gestión de productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoHateoasService productoHateoasService;

    @Operation(summary = "Obtener todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoModel.class)) })
    })
    @GetMapping
    public CollectionModel<ProductoModel> listarProductos() {
        List<ProductoModel> productos = productoService.listarProductos().stream()
                .map(productoHateoasService::toModel)
                .collect(Collectors.toList());
        return productoHateoasService.toCollectionModel(productos);
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductoModel> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        ProductoModel model = productoHateoasService.toModel(nuevoProducto);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoModel> buscarProducto(@PathVariable int id) {
        Producto producto = productoService.buscarProducto(id);
        return ResponseEntity.ok(productoHateoasService.toModel(producto));
    }

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoModel> actualizarProducto(@PathVariable int id, @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(productoHateoasService.toModel(productoActualizado));
    }

    @Operation(summary = "Actualizar parcialmente un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductoModel> actualizarParcialProducto(@PathVariable int id, @RequestBody Producto producto) {
        Producto productoExistente = productoService.buscarProducto(id);
        if (producto.getNombre() != null) {
            productoExistente.setNombre(producto.getNombre());
        }
        if (producto.getStock() != 0) {
            productoExistente.setStock(producto.getStock());
        }
        if (producto.getPrecio() != 0) {
            productoExistente.setPrecio(producto.getPrecio());
        }
        Producto productoActualizado = productoService.actualizarProducto(id, productoExistente);
        return ResponseEntity.ok(productoHateoasService.toModel(productoActualizado));
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
