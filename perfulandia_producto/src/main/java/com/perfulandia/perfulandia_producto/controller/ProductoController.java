package com.perfulandia.perfulandia_producto.controller;

import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.crearProducto(producto);
    }

    @GetMapping("/{id}")
    public Producto buscarProducto(@PathVariable int id) {
        return productoService.buscarProducto(id);
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable int id, @RequestBody Producto producto) {
        return productoService.actualizarProducto(id, producto);
    }

    @PatchMapping("/{id}")
    public Producto actualizarParcialProducto(@PathVariable int id, @RequestBody Producto producto) {
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
        return productoService.actualizarProducto(id, productoExistente);
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable int id) {
        productoService.eliminarProducto(id);
    }
}
