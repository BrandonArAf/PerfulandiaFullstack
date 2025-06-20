package com.perfulandia.pago.controller;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.service.PagoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pago> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Pago obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Pago crear(@RequestBody Pago pago) {
        return service.guardar(pago);
    }

    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Long id, @RequestBody Pago pago) {
        pago.setId(id);
        return service.guardar(pago);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}