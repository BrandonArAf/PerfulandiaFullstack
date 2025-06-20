package com.perfulandia.perfulandia_pedido.controller;

import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.service.PedidoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Pedido crear(@RequestBody Pedido pedido) {
        return service.guardar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        pedido.setId(id);
        return service.actualizar(pedido);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
