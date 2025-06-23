package com.perfulandia.inventario.controller;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.model.InventarioModel;
import com.perfulandia.inventario.service.InventarioHateoasService;
import com.perfulandia.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;

/**
 * Controlador REST para la gestión de inventario.
 * Esta clase expone endpoints HTTP para realizar operaciones CRUD (Create, Read, Update, Delete)
 * sobre los registros de inventario, incluyendo operaciones específicas como aumentar/reducir stock.
 * 
 * El controlador implementa:
 * - Documentación OpenAPI con anotaciones Swagger
 * - Respuestas HATEOAS con enlaces navegables
 * - Manejo de excepciones con códigos HTTP apropiados
 * - Validación de datos de entrada
 */
@RestController // Marca esta clase como un controlador REST que maneja peticiones HTTP
@RequestMapping("/api/v1/inventario") // Define el prefijo base para todos los endpoints de este controlador
@Tag(name = "Inventario", description = "API para la gestión de inventario") // Anotación OpenAPI para agrupar endpoints en la documentación
public class InventarioController {
    
    @Autowired // Inyección de dependencias automática del servicio de inventario
    private InventarioService service;
    
    @Autowired // Inyección de dependencias automática del servicio HATEOAS
    private InventarioHateoasService inventarioHateoasService;

    /**
     * Obtiene todos los registros de inventario.
     * Este endpoint retorna una lista completa de todos los registros de inventario
     * con enlaces HATEOAS para navegación.
     * 
     * @return CollectionModel con todos los registros de inventario y enlaces HATEOAS
     */
    @Operation(summary = "Obtener todo el inventario") // Anotación OpenAPI que describe la operación
    @ApiResponses(value = { // Anotación OpenAPI que documenta las posibles respuestas
            @ApiResponse(responseCode = "200", description = "Inventario encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }) // Define el esquema de respuesta
    })
    @GetMapping // Mapea peticiones HTTP GET a este método
    public CollectionModel<InventarioModel> getAll() {
        // Obtiene todos los registros de inventario del servicio
        List<InventarioModel> inventarios = service.findAll().stream()
                .map(inventarioHateoasService::toModel) // Convierte cada entidad a un modelo HATEOAS
                .collect(Collectors.toList()); // Recolecta los resultados en una lista
        
        // Retorna una colección HATEOAS con enlaces para navegación
        return inventarioHateoasService.toCollectionModel(inventarios);
    }

    /**
     * Obtiene un registro de inventario específico por ID de producto.
     * Este endpoint busca un registro de inventario usando el ID del producto
     * y retorna el registro con enlaces HATEOAS si existe.
     * 
     * @param productoId ID del producto para buscar en inventario
     * @return ResponseEntity con el registro de inventario o 404 si no existe
     */
    @Operation(summary = "Obtener inventario por ID de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content) // No hay contenido en la respuesta de error
    })
    @GetMapping("/{productoId}") // Mapea peticiones GET con un parámetro de ruta
    public ResponseEntity<InventarioModel> getByProductoId(@PathVariable Long productoId) {
        try {
            // Intenta buscar el inventario por ID de producto
            Inventario inventario = service.findByProductoId(productoId);
            // Si existe, retorna el modelo HATEOAS con código 200
            return ResponseEntity.ok(inventarioHateoasService.toModel(inventario));
        } catch (RuntimeException e) {
            // Si no existe, retorna código 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo registro de inventario.
     * Este endpoint recibe los datos de un nuevo registro de inventario
     * y lo guarda en la base de datos.
     * 
     * @param inventario Datos del nuevo registro de inventario
     * @return ResponseEntity con el registro creado y código 201 (Created)
     */
    @Operation(summary = "Crear nuevo registro de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PostMapping // Mapea peticiones HTTP POST a este método
    public ResponseEntity<InventarioModel> create(@RequestBody Inventario inventario) {
        // Guarda el nuevo inventario en la base de datos
        Inventario nuevoInventario = service.save(inventario);
        // Convierte a modelo HATEOAS
        InventarioModel model = inventarioHateoasService.toModel(nuevoInventario);
        // Retorna respuesta 201 (Created) con la ubicación del nuevo recurso
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    /**
     * Aumenta la cantidad disponible de un producto en inventario.
     * Este endpoint permite incrementar el stock de un producto específico.
     * 
     * @param productoId ID del producto cuyo stock se va a aumentar
     * @param cantidad Cantidad a aumentar en el inventario
     * @return ResponseEntity con el inventario actualizado o 404 si no existe
     */
    @Operation(summary = "Aumentar cantidad en inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad aumentada",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content)
    })
    @PutMapping("/{productoId}/aumentar") // Mapea peticiones PUT con parámetro de ruta y query
    public ResponseEntity<InventarioModel> aumentar(@PathVariable Long productoId, @RequestParam int cantidad) {
        try {
            // Aumenta la cantidad en el inventario
            Inventario inventario = service.ajustarCantidad(productoId, cantidad);
            // Retorna el inventario actualizado con enlaces HATEOAS
            return ResponseEntity.ok(inventarioHateoasService.toModel(inventario));
        } catch (RuntimeException e) {
            // Si no existe el inventario, retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reduce la cantidad disponible de un producto en inventario.
     * Este endpoint permite decrementar el stock de un producto específico.
     * 
     * @param productoId ID del producto cuyo stock se va a reducir
     * @param cantidad Cantidad a reducir en el inventario
     * @return ResponseEntity con el inventario actualizado o 404 si no existe
     */
    @Operation(summary = "Reducir cantidad en inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad reducida",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content)
    })
    @PutMapping("/{productoId}/reducir") // Mapea peticiones PUT con parámetro de ruta y query
    public ResponseEntity<InventarioModel> reducir(@PathVariable Long productoId, @RequestParam int cantidad) {
        try {
            // Reduce la cantidad en el inventario (pasa cantidad negativa)
            Inventario inventario = service.ajustarCantidad(productoId, -cantidad);
            // Retorna el inventario actualizado con enlaces HATEOAS
            return ResponseEntity.ok(inventarioHateoasService.toModel(inventario));
        } catch (RuntimeException e) {
            // Si no existe el inventario, retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza completamente un registro de inventario.
     * Este endpoint permite modificar todos los campos de un registro de inventario.
     * 
     * @param productoId ID del producto cuyo inventario se va a actualizar
     * @param data Nuevos datos del inventario
     * @return ResponseEntity con el inventario actualizado o 404 si no existe
     */
    @Operation(summary = "Actualizar inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PutMapping("/{productoId}") // Mapea peticiones PUT con parámetro de ruta
    public ResponseEntity<InventarioModel> update(@PathVariable Long productoId, @RequestBody Inventario data) {
        try {
            // Busca el inventario existente
            Inventario inventario = service.findByProductoId(productoId);
            // Actualiza los campos con los nuevos datos
            inventario.setCantidadDisponible(data.getCantidadDisponible());
            inventario.setUbicacion(data.getUbicacion());
            // Guarda los cambios
            Inventario inventarioActualizado = service.save(inventario);
            // Retorna el inventario actualizado con enlaces HATEOAS
            return ResponseEntity.ok(inventarioHateoasService.toModel(inventarioActualizado));
        } catch (RuntimeException e) {
            // Si no existe el inventario, retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza parcialmente un registro de inventario.
     * Este endpoint permite modificar solo algunos campos específicos del inventario
     * usando reflexión para acceder dinámicamente a los campos.
     * 
     * @param id ID del registro de inventario a actualizar
     * @param updates Mapa con los campos y valores a actualizar
     * @return ResponseEntity con el inventario actualizado o 404 si no existe
     */
    @Operation(summary = "Actualizar parcialmente inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PatchMapping("/{id}") // Mapea peticiones HTTP PATCH para actualizaciones parciales
    public ResponseEntity<InventarioModel> patchInventario(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            // Busca el inventario existente
            Inventario inventario = service.findById(id);
            // Itera sobre cada campo a actualizar
            updates.forEach((key, value) -> {
                // Usa reflexión para encontrar el campo en la clase
                Field field = ReflectionUtils.findField(Inventario.class, key);
                if (field != null) {
                    // Hace el campo accesible (necesario para campos privados)
                    field.setAccessible(true);
                    // Establece el nuevo valor usando reflexión
                    ReflectionUtils.setField(field, inventario, value);
                }
            });
            // Guarda los cambios
            Inventario inventarioActualizado = service.save(inventario);
            // Retorna el inventario actualizado con enlaces HATEOAS
            return ResponseEntity.ok(inventarioHateoasService.toModel(inventarioActualizado));
        } catch (RuntimeException e) {
            // Si no existe el inventario, retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un registro de inventario.
     * Este endpoint elimina permanentemente un registro de inventario de la base de datos.
     * 
     * @param id ID del registro de inventario a eliminar
     * @return ResponseEntity con código 204 (No Content) si se eliminó exitosamente
     */
    @Operation(summary = "Eliminar inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventario eliminado"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @DeleteMapping("/{id}") // Mapea peticiones HTTP DELETE
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            // Verifica que el inventario existe antes de eliminarlo
            service.findById(id);
            // Elimina el inventario
            service.deleteById(id);
            // Retorna código 204 (No Content) indicando éxito sin contenido
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Si no existe el inventario, retorna 404
            return ResponseEntity.notFound().build();
        }
    }
}
