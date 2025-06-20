package com.perfulandia.perfulandia_pedido.service;

import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.Producto;
import com.perfulandia.perfulandia_pedido.model.InventarioResponse;
import com.perfulandia.perfulandia_pedido.model.PagoRequest;
import com.perfulandia.perfulandia_pedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.perfulandia.perfulandia_pedido.config.RabbitMQConfig;

import java.util.Arrays;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido crearPedido(Pedido pedido) {
        // Suponiendo que pedido.getCliente() es el ID del usuario (ajusta si es necesario)
        int usuarioId = Integer.parseInt(pedido.getCliente());
        if (!usuarioExiste(usuarioId)) {
            throw new RuntimeException("El usuario no existe, no se puede crear el pedido.");
        }
        Pedido nuevoPedido = pedidoRepository.save(pedido);
        notificarInventario(Long.parseLong(nuevoPedido.getProducto()), nuevoPedido.getCantidad());
        return nuevoPedido;
    }

    public Pedido buscarPedido(int id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public Pedido actualizarPedido(int id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setCliente(pedido.getCliente());
            existente.setProducto(pedido.getProducto());
            existente.setCantidad(pedido.getCantidad());
            existente.setTotal(pedido.getTotal());
            existente.setFecha(pedido.getFecha());
            return pedidoRepository.save(existente);
        }
        return null;
    }

    public void eliminarPedido(int id) {
        pedidoRepository.deleteById(id);
    }

    // --- Nuevo método para consumir productos ---
    public List<Producto> obtenerProductos() {
        String url = "http://localhost:8081/api/v1/productos";
        Producto[] productos = restTemplate.getForObject(url, Producto[].class);
        return Arrays.asList(productos);
    }

    // Consultar stock de un producto en el microservicio de inventario
    public Integer consultarStockProducto(Long productoId) {
        String url = "http://localhost:8083/api/inventario/" + productoId;
        try {
            InventarioResponse inv = restTemplate.getForObject(url, InventarioResponse.class);
            return inv != null ? inv.getCantidadDisponible() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public void registrarPago(PagoRequest pagoRequest) {
        String url = "http://localhost:8084/api/pagos";
        restTemplate.postForObject(url, pagoRequest, Void.class);
    }

    public boolean usuarioExiste(int usuarioId) {
        String url = "http://localhost:8080/api/v1/usuarios/" + usuarioId;
        try {
            // Si el usuario existe, devuelve un objeto; si no, lanza excepción o devuelve null
            Object usuario = restTemplate.getForObject(url, Object.class);
            return usuario != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void notificarInventario(Long productoId, int cantidad) {
        String mensaje = productoId + ":" + cantidad;
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, mensaje);
    }
}
