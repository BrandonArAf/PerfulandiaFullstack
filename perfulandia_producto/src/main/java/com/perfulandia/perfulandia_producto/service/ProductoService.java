
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
        return productoRepository.findById(id).orElse(null);
    }

    public Producto actualizarProducto(int id, Producto producto) {
        Producto existente = productoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setStock(producto.getStock());
            existente.setPrecio(producto.getPrecio());
            return productoRepository.save(existente);
        }
        return null;
    }

    public void eliminarProducto(int id) {
        productoRepository.deleteById(id);
    }
}
