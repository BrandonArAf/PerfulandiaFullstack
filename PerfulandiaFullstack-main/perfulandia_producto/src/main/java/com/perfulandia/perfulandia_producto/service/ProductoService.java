package com.perfulandia.perfulandia_producto.service;

import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto buscarProducto(int id) {
        return productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    public Producto actualizarProducto(int id, Producto producto) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        producto.setId(id);
        return productoRepository.save(producto);
    }

    public void eliminarProducto(int id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
}
