package com.perfulandia.inventario.controller;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.service.InventarioService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {
    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Inventario> listarTodos() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Inventario obtener(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Inventario crear(@RequestBody Inventario inventario) {
        return service.save(inventario);
    }

    @PutMapping("/{id}")
    public Inventario actualizar(@PathVariable Long id, @RequestBody Inventario inventario) {
        inventario.setId(id);
        return service.save(inventario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.deleteById(id);
    }
}
