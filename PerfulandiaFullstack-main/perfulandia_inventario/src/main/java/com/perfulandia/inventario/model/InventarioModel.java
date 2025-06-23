package com.perfulandia.inventario.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

/**
 * Modelo HATEOAS para la entidad Inventario.
 * Esta clase extiende RepresentationModel para agregar enlaces HATEOAS a los recursos de inventario.
 * HATEOAS (Hypermedia as the Engine of Application State) permite que las respuestas de la API
 * incluyan enlaces navegables para descubrir y acceder a otros recursos relacionados.
 */
@Data // Anotación de Lombok que genera getters, setters, toString, equals y hashCode
@EqualsAndHashCode(callSuper = true) // Genera equals y hashCode incluyendo los campos de la clase padre
public class InventarioModel extends RepresentationModel<InventarioModel> {
    
    private Long id; // Identificador único del registro de inventario
    
    private Long productoId; // ID del producto al que pertenece este registro de inventario
    
    private Integer cantidadDisponible; // Cantidad de unidades disponibles en stock
    
    private String ubicacion; // Ubicación física del producto en el almacén

    /**
     * Constructor que convierte una entidad Inventario a un modelo HATEOAS.
     * Este constructor toma los datos de la entidad y los copia al modelo,
     * preparándolo para recibir enlaces HATEOAS.
     * 
     * @param inventario La entidad Inventario de la base de datos
     */
    public InventarioModel(Inventario inventario) {
        this.id = inventario.getId(); // Copia el ID de la entidad
        this.productoId = inventario.getProductoId(); // Copia el ID del producto
        this.cantidadDisponible = inventario.getCantidadDisponible(); // Copia la cantidad disponible
        this.ubicacion = inventario.getUbicacion(); // Copia la ubicación
    }
} 