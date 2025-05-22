package com.perfulandia.inventario.controller;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Inventario> getAll() {
        return service.findAll();
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<Inventario> getByProductoId(@PathVariable Long productoId) {
        Inventario inv = service.findByProductoId(productoId);
        return inv != null ? ResponseEntity.ok(inv) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Inventario create(@RequestBody Inventario inventario) {
        return service.save(inventario);
    }

    @PutMapping("/{productoId}/aumentar")
    public ResponseEntity<Inventario> aumentar(@PathVariable Long productoId, @RequestParam int cantidad) {
        Inventario inv = service.ajustarCantidad(productoId, cantidad);
        return inv != null ? ResponseEntity.ok(inv) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{productoId}/reducir")
    public ResponseEntity<Inventario> reducir(@PathVariable Long productoId, @RequestParam int cantidad) {
        Inventario inv = service.ajustarCantidad(productoId, -cantidad);
        return inv != null ? ResponseEntity.ok(inv) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Inventario> update(@PathVariable Long productoId, @RequestBody Inventario data) {
        Inventario inv = service.findByProductoId(productoId);
        if (inv != null) {
            inv.setCantidadDisponible(data.getCantidadDisponible());
            inv.setUbicacion(data.getUbicacion());
            return ResponseEntity.ok(service.save(inv));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Inventario> patchInventario(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Inventario inventario = service.findById(id);
        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Inventario.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, inventario, value);
            }
        });
        return ResponseEntity.ok(service.save(inventario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Inventario inventario = service.findById(id);
        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
