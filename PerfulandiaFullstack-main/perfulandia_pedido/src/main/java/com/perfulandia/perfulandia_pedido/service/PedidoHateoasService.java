package com.perfulandia.perfulandia_pedido.service;

import com.perfulandia.perfulandia_pedido.controller.PedidoController;
import com.perfulandia.perfulandia_pedido.model.Pedido;
import com.perfulandia.perfulandia_pedido.model.PedidoModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servicio HATEOAS para Pedidos
 * 
 * Este servicio se encarga de agregar enlaces navegables a los modelos de pedidos,
 * permitiendo que los clientes descubran y naveguen por la API de manera dinámica.
 */
@Service
public class PedidoHateoasService {

    /**
     * Convierte una entidad Pedido a PedidoModel con enlaces HATEOAS
     */
    public PedidoModel toModel(Pedido pedido) {
        PedidoModel model = new PedidoModel(pedido);
        
        // Enlace a sí mismo
        model.add(linkTo(methodOn(PedidoController.class).buscarPedido(pedido.getId())).withSelfRel());
        
        // Enlace a la colección de pedidos
        model.add(linkTo(methodOn(PedidoController.class).listarPedidos()).withRel("pedidos"));
        
        // Enlaces de acciones disponibles
        model.add(linkTo(methodOn(PedidoController.class).actualizarPedido(pedido.getId(), null)).withRel("update"));
        model.add(Link.of("/api/v1/pedido/" + pedido.getId()).withRel("delete"));
        model.add(linkTo(methodOn(PedidoController.class).actualizarParcialmentePedido(pedido.getId(), null)).withRel("patch"));
        
        // Enlaces a recursos relacionados
        if (pedido.getProducto() != null) {
            model.add(linkTo(methodOn(PedidoController.class).consultarStockProducto(Long.parseLong(pedido.getProducto()))).withRel("stock"));
            model.add(linkTo(methodOn(PedidoController.class).verificarStock(Long.parseLong(pedido.getProducto()), pedido.getCantidad())).withRel("verificar-stock"));
        }
        
        // Enlace a productos externos
        model.add(linkTo(methodOn(PedidoController.class).obtenerProductosExternos()).withRel("productos"));
        
        return model;
    }

    /**
     * Convierte una lista de entidades Pedido a lista de PedidoModel con enlaces HATEOAS
     */
    public List<PedidoModel> toModelList(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    /**
     * Crea un modelo de colección con enlaces HATEOAS
     */
    public CollectionModel<PedidoModel> toCollectionModel(List<Pedido> pedidos) {
        List<PedidoModel> models = toModelList(pedidos);
        
        CollectionModel<PedidoModel> collectionModel = CollectionModel.of(models);
        
        // Enlace a la colección
        collectionModel.add(linkTo(methodOn(PedidoController.class).listarPedidos()).withSelfRel());
        
        // Enlace para crear nuevo pedido
        collectionModel.add(linkTo(methodOn(PedidoController.class).crearPedido(null)).withRel("create"));
        
        // Enlaces a recursos relacionados
        collectionModel.add(linkTo(methodOn(PedidoController.class).obtenerProductosExternos()).withRel("productos"));
        
        return collectionModel;
    }
} 