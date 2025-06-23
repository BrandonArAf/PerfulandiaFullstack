package com.perfulandia.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un registro de inventario en la base de datos.
 * Esta clase mapea la tabla "inventario" y contiene la información de stock de productos.
 */
@Entity // Anotación que indica que esta clase es una entidad JPA que se mapea a una tabla en la base de datos
@Table(name = "inventario") // Especifica el nombre de la tabla en la base de datos
@Data // Anotación de Lombok que genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Inventario {
    
    @Id // Marca este campo como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera automáticamente valores únicos para el ID (auto-incremento)
    private Long id; // Identificador único del registro de inventario

    @Column(name = "producto_id") // Especifica el nombre de la columna en la base de datos
    private Long productoId; // ID del producto al que pertenece este registro de inventario
    
    private Integer cantidadDisponible; // Cantidad de unidades disponibles en stock para este producto
    
    private String ubicacion; // Ubicación física donde se almacena el producto (ej: "Almacén A", "Estante 3")
}