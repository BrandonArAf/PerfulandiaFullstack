package com.perfulandia.pago.controller;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    @Autowired
    private PagoService service;

    @GetMapping
    public List<Pago> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> getById(@PathVariable Long id) {
        Pago pago = service.findById(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Pago create(@RequestBody Pago pago) {
        return service.save(pago);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> update(@PathVariable Long id, @RequestBody Pago data) {
        Pago existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setPedidoId(data.getPedidoId());
        existing.setMonto(data.getMonto());
        existing.setMetodo(data.getMetodo());
        existing.setEstado(data.getEstado());
        return ResponseEntity.ok(service.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Pago pago = service.findById(id);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pago> patchPago(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Pago pago = service.findById(id);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Pago.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pago, value);
            }
        });
        return ResponseEntity.ok(service.save(pago));
    }
}
