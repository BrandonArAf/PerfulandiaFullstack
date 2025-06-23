package com.perfulandia.pago.service;

import com.perfulandia.pago.controller.PagoController;
import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.model.PagoModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servicio HATEOAS para Pagos
 * 
 * Este servicio se encarga de agregar enlaces navegables a los modelos de pagos,
 * permitiendo que los clientes descubran y naveguen por la API de manera dinámica.
 */
@Service
public class PagoHateoasService {

    /**
     * Convierte una entidad Pago a PagoModel con enlaces HATEOAS
     */
    public PagoModel toModel(Pago pago) {
        PagoModel model = new PagoModel(pago);
        
        // Enlace a sí mismo
        model.add(linkTo(methodOn(PagoController.class).getById(pago.getId())).withSelfRel());
        
        // Enlace a la colección de pagos
        model.add(linkTo(methodOn(PagoController.class).getAll()).withRel("pagos"));
        
        // Enlaces de acciones disponibles
        model.add(linkTo(methodOn(PagoController.class).update(pago.getId(), null)).withRel("update"));
        model.add(Link.of("/api/v1/pago/" + pago.getId()).withRel("delete"));
        
        return model;
    }

    /**
     * Convierte una lista de entidades Pago a lista de PagoModel con enlaces HATEOAS
     */
    public List<PagoModel> toModelList(List<Pago> pagos) {
        return pagos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    /**
     * Crea un modelo de colección con enlaces HATEOAS
     */
    public CollectionModel<PagoModel> toCollectionModel(List<Pago> pagos) {
        List<PagoModel> models = toModelList(pagos);
        
        CollectionModel<PagoModel> collectionModel = CollectionModel.of(models);
        
        // Enlace a la colección
        collectionModel.add(linkTo(methodOn(PagoController.class).getAll()).withSelfRel());
        
        // Enlace para crear nuevo pago
        collectionModel.add(linkTo(methodOn(PagoController.class).create(null)).withRel("create"));
        
        return collectionModel;
    }
} 