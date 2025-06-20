package com.perfulandia.inventario.service;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    // Método extra para RabbitMQ
    public void ajustarCantidad(Long productoId, int cantidad) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId);
        if (inventario != null) {
            inventario.setCantidad(inventario.getCantidad() + cantidad);
            inventarioRepository.save(inventario);
        }
    }
}
