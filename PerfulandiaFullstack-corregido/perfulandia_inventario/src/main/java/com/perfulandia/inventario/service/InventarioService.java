package com.perfulandia.inventario.service;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {
    private final InventarioRepository repository;

    public InventarioService(InventarioRepository repository) {
        this.repository = repository;
    }

    public List<Inventario> findAll() {
        return repository.findAll();
    }

    public Inventario findByProductoId(Long productoId) {
        return repository.findByProductoId(productoId);
    }

    public Inventario findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Inventario save(Inventario inventario) {
        return repository.save(inventario);
    }

    // MÉTODO QUE FALTABA: Ajustar cantidad de inventario por productoId
    public Inventario ajustarCantidad(Long productoId, int cantidad) {
        Inventario inventario = repository.findByProductoId(productoId);
        if (inventario != null) {
            inventario.setCantidadDisponible(inventario.getCantidadDisponible() + cantidad);
            return repository.save(inventario);
        }
        return null;
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}