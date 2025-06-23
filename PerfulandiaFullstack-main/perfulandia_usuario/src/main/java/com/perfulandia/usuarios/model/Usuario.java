package com.perfulandia.usuarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un usuario en la base de datos.
 * Esta clase mapea la tabla "usuarios" y contiene la información básica de un usuario
 * del sistema Perfulandia, incluyendo credenciales de autenticación.
 * 
 * La entidad implementa:
 * - Validaciones de campos obligatorios
 * - Restricción de unicidad para el correo electrónico
 * - Generación automática de ID
 */
@Entity // Anotación que indica que esta clase es una entidad JPA que se mapea a una tabla en la base de datos
@Table(name = "usuarios") // Especifica el nombre de la tabla en la base de datos
@Data // Anotación de Lombok que genera automáticamente getters, setters, toString, equals y hashCode
@AllArgsConstructor // Genera un constructor con todos los argumentos
@NoArgsConstructor // Genera un constructor sin argumentos (requerido por JPA)
public class Usuario {
    
    @Id // Marca este campo como la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera automáticamente valores únicos para el ID (auto-incremento)
    private int id; // Identificador único del usuario en el sistema

    @Column(nullable = false) // Especifica que este campo no puede ser null en la base de datos
    private String nombre; // Nombre completo del usuario

    @Column(nullable = false, unique = true) // Campo obligatorio y único (no puede repetirse)
    private String correo; // Dirección de correo electrónico del usuario (usado para login)

    @Column(nullable = false) // Especifica que este campo no puede ser null en la base de datos
    private String contrasena; // Contraseña del usuario (en producción debería estar encriptada)
}
