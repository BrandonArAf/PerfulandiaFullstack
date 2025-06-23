package com.perfulandia.perfulandia_producto.service;

import com.perfulandia.perfulandia_producto.controller.ProductoController;
import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.model.ProductoModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductoHateoasService {

    public ProductoModel toModel(Producto producto) {
        ProductoModel model = new ProductoModel(producto);
        model.add(linkTo(methodOn(ProductoController.class).buscarProducto(producto.getId())).withSelfRel());
        model.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        return model;
    }

    public CollectionModel<ProductoModel> toCollectionModel(List<ProductoModel> productos) {
        CollectionModel<ProductoModel> collectionModel = CollectionModel.of(productos);
        collectionModel.add(linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
        collectionModel.add(linkTo(methodOn(ProductoController.class).crearProducto(null)).withRel("create"));
        return collectionModel;
    }
} 