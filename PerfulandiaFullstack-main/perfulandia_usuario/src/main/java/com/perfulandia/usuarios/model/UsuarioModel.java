package com.perfulandia.usuarios.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Modelo HATEOAS para la entidad Usuario.
 * Esta clase extiende RepresentationModel para agregar enlaces HATEOAS a los recursos de usuario.
 * HATEOAS (Hypermedia as the Engine of Application State) permite que las respuestas de la API
 * incluyan enlaces navegables para descubrir y acceder a otros recursos relacionados.
 * 
 * El modelo implementa:
 * - Conversión bidireccional entre entidad y modelo
 * - Configuración de relaciones para documentación OpenAPI
 * - Exclusión de campos null en respuestas JSON
 */
@Relation(collectionRelation = "usuarios", itemRelation = "usuario") // Define las relaciones para OpenAPI
@JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos null de las respuestas JSON
public class UsuarioModel extends RepresentationModel<UsuarioModel> {
    
    private Integer id; // Identificador único del usuario
    private String nombre; // Nombre completo del usuario
    private String correo; // Dirección de correo electrónico
    private String contrasena; // Contraseña del usuario (en producción debería estar encriptada)

    /**
     * Constructor vacío requerido para la deserialización JSON.
     * Este constructor es necesario para que Jackson pueda crear instancias
     * del modelo desde JSON en las peticiones HTTP.
     */
    public UsuarioModel() {}

    /**
     * Constructor con todos los campos.
     * Permite crear un modelo HATEOAS especificando todos los valores.
     * 
     * @param id Identificador único del usuario
     * @param nombre Nombre completo del usuario
     * @param correo Dirección de correo electrónico
     * @param contrasena Contraseña del usuario
     */
    public UsuarioModel(Integer id, String nombre, String correo, String contrasena) {
        this.id = id; // Asigna el ID del usuario
        this.nombre = nombre; // Asigna el nombre del usuario
        this.correo = correo; // Asigna el correo del usuario
        this.contrasena = contrasena; // Asigna la contraseña del usuario
    }

    /**
     * Constructor que convierte una entidad Usuario a un modelo HATEOAS.
     * Este constructor toma los datos de la entidad y los copia al modelo,
     * preparándolo para recibir enlaces HATEOAS.
     * 
     * @param usuario La entidad Usuario de la base de datos
     */
    public UsuarioModel(Usuario usuario) {
        this.id = usuario.getId(); // Copia el ID de la entidad
        this.nombre = usuario.getNombre(); // Copia el nombre de la entidad
        this.correo = usuario.getCorreo(); // Copia el correo de la entidad
        this.contrasena = usuario.getContrasena(); // Copia la contraseña de la entidad
    }

    /**
     * Convierte el modelo HATEOAS de vuelta a una entidad Usuario.
     * Este método es útil cuando se necesita guardar los datos del modelo
     * en la base de datos.
     * 
     * @return Entidad Usuario con los datos del modelo
     */
    public Usuario toEntity() {
        Usuario usuario = new Usuario(); // Crea una nueva entidad
        usuario.setId(this.id); // Establece el ID
        usuario.setNombre(this.nombre); // Establece el nombre
        usuario.setCorreo(this.correo); // Establece el correo
        usuario.setContrasena(this.contrasena); // Establece la contraseña
        return usuario; // Retorna la entidad creada
    }

    // ========== GETTERS Y SETTERS ==========
    
    /**
     * Obtiene el identificador único del usuario.
     * @return ID del usuario
     */
    public Integer getId() { return id; }
    
    /**
     * Establece el identificador único del usuario.
     * @param id ID del usuario
     */
    public void setId(Integer id) { this.id = id; }
    
    /**
     * Obtiene el nombre completo del usuario.
     * @return Nombre del usuario
     */
    public String getNombre() { return nombre; }
    
    /**
     * Establece el nombre completo del usuario.
     * @param nombre Nombre del usuario
     */
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    /**
     * Obtiene la dirección de correo electrónico del usuario.
     * @return Correo del usuario
     */
    public String getCorreo() { return correo; }
    
    /**
     * Establece la dirección de correo electrónico del usuario.
     * @param correo Correo del usuario
     */
    public void setCorreo(String correo) { this.correo = correo; }
    
    /**
     * Obtiene la contraseña del usuario.
     * @return Contraseña del usuario
     */
    public String getContrasena() { return contrasena; }
    
    /**
     * Establece la contraseña del usuario.
     * @param contrasena Contraseña del usuario
     */
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
} 