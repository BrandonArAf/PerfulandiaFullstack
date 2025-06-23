package com.perfulandia.inventario.service;

import com.perfulandia.inventario.controller.InventarioController;
import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.model.InventarioModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servicio HATEOAS para la entidad Inventario.
 * Esta clase se encarga de agregar enlaces HATEOAS a los modelos de inventario,
 * permitiendo que las respuestas de la API incluyan enlaces navegables.
 * 
 * HATEOAS (Hypermedia as the Engine of Application State) es un principio de REST
 * que permite que las APIs sean auto-documentadas y navegables.
 */
@Service // Marca esta clase como un servicio de Spring, permitiendo la inyección de dependencias
public class InventarioHateoasService {

    /**
     * Convierte una entidad Inventario a un modelo HATEOAS con enlaces.
     * Este método toma una entidad de inventario y la convierte en un modelo
     * que incluye enlaces HATEOAS para navegación.
     * 
     * @param inventario La entidad Inventario de la base de datos
     * @return InventarioModel con enlaces HATEOAS agregados
     */
    public InventarioModel toModel(Inventario inventario) {
        // Crea un nuevo modelo HATEOAS a partir de la entidad
        InventarioModel model = new InventarioModel(inventario);
        
        // Agrega un enlace "self" que apunta al recurso actual
        // linkTo() genera la URL del endpoint, methodOn() especifica el método del controlador
        model.add(linkTo(methodOn(InventarioController.class).getByProductoId(inventario.getProductoId())).withSelfRel());
        
        // Agrega un enlace "inventario" que apunta a la colección de inventario
        model.add(linkTo(methodOn(InventarioController.class).getAll()).withRel("inventario"));
        
        return model; // Retorna el modelo con los enlaces HATEOAS
    }

    /**
     * Convierte una lista de modelos de inventario a una colección HATEOAS.
     * Este método toma una lista de modelos y la envuelve en un CollectionModel
     * que incluye enlaces HATEOAS para la colección completa.
     * 
     * @param inventarios Lista de modelos de inventario
     * @return CollectionModel con enlaces HATEOAS para la colección
     */
    public CollectionModel<InventarioModel> toCollectionModel(List<InventarioModel> inventarios) {
        // Crea un CollectionModel con la lista de inventarios
        CollectionModel<InventarioModel> collectionModel = CollectionModel.of(inventarios);
        
        // Agrega un enlace "self" que apunta a la colección actual
        collectionModel.add(linkTo(methodOn(InventarioController.class).getAll()).withSelfRel());
        
        // Agrega un enlace "create" que permite crear nuevos registros de inventario
        collectionModel.add(linkTo(methodOn(InventarioController.class).create(null)).withRel("create"));
        
        return collectionModel; // Retorna la colección con los enlaces HATEOAS
    }
} 