package com.perfulandia.pago.controller;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.model.PagoModel;
import com.perfulandia.pago.service.PagoHateoasService;
import com.perfulandia.pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;

/**
 * Controlador REST para la gestión de pagos
 * 
 * Este controlador proporciona endpoints para realizar operaciones CRUD
 * sobre los pagos, incluyendo enlaces HATEOAS para navegación dinámica.
 */
@RestController
@RequestMapping("/api/v1/pago")
@Tag(name = "Pagos", description = "API para gestión de pagos en Perfulandia")
public class PagoController {
    
    @Autowired
    private PagoService service;
    
    @Autowired
    private PagoHateoasService hateoasService;

    @GetMapping
    @Operation(
        summary = "Obtener todos los pagos",
        description = "Retorna una lista de todos los pagos registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de pagos obtenida exitosamente",
            content = @Content(
                mediaType = "application/hal+json",
                schema = @Schema(implementation = CollectionModel.class),
                examples = @ExampleObject(
                    name = "Lista de pagos",
                    value = """
                    {
                      "_embedded": {
                        "pagoList": [
                          {
                            "id": 1,
                            "pedidoId": 123,
                            "monto": 150.00,
                            "metodo": "TARJETA",
                            "estado": "COMPLETADO",
                            "_links": {
                              "self": {"href": "/api/v1/pago/1"},
                              "pagos": {"href": "/api/v1/pago"},
                              "update": {"href": "/api/v1/pago/1"},
                              "delete": {"href": "/api/v1/pago/1"}
                            }
                          }
                        ]
                      },
                      "_links": {
                        "self": {"href": "/api/v1/pago"},
                        "create": {"href": "/api/v1/pago"}
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<CollectionModel<PagoModel>> getAll() {
        List<Pago> pagos = service.findAll();
        CollectionModel<PagoModel> collectionModel = hateoasService.toCollectionModel(pagos);
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener pago por ID",
        description = "Retorna un pago específico basado en su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pago encontrado exitosamente",
            content = @Content(
                mediaType = "application/hal+json",
                schema = @Schema(implementation = PagoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pago no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<PagoModel> getById(
            @Parameter(description = "ID del pago a buscar", example = "1")
            @PathVariable Long id) {
        Pago pago = service.findById(id);
        if (pago != null) {
            PagoModel model = hateoasService.toModel(pago);
            return ResponseEntity.ok(model);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo pago",
        description = "Crea un nuevo pago en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Pago creado exitosamente",
            content = @Content(
                mediaType = "application/hal+json",
                schema = @Schema(implementation = PagoModel.class),
                examples = @ExampleObject(
                    name = "Pago creado",
                    value = """
                    {
                      "id": 1,
                      "pedidoId": 123,
                      "monto": 150.00,
                      "metodo": "TARJETA",
                      "estado": "PENDIENTE",
                      "_links": {
                        "self": {"href": "/api/v1/pago/1"},
                        "pagos": {"href": "/api/v1/pago"},
                        "update": {"href": "/api/v1/pago/1"},
                        "delete": {"href": "/api/v1/pago/1"}
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de pago inválidos",
            content = @Content
        )
    })
    public ResponseEntity<PagoModel> create(
            @Parameter(description = "Datos del pago a crear")
            @RequestBody Pago pago) {
        Pago savedPago = service.save(pago);
        PagoModel model = hateoasService.toModel(savedPago);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar pago existente",
        description = "Actualiza un pago existente basado en su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pago actualizado exitosamente",
            content = @Content(
                mediaType = "application/hal+json",
                schema = @Schema(implementation = PagoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pago no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<PagoModel> update(
            @Parameter(description = "ID del pago a actualizar", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del pago")
            @RequestBody Pago data) {
        Pago existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setPedidoId(data.getPedidoId());
        existing.setMonto(data.getMonto());
        existing.setMetodo(data.getMetodo());
        existing.setEstado(data.getEstado());
        Pago updatedPago = service.save(existing);
        PagoModel model = hateoasService.toModel(updatedPago);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar pago",
        description = "Elimina un pago basado en su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Pago eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pago no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del pago a eliminar", example = "1")
            @PathVariable Long id) {
        Pago pago = service.findById(id);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar parcialmente pago",
        description = "Actualiza campos específicos de un pago existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pago actualizado parcialmente exitosamente",
            content = @Content(
                mediaType = "application/hal+json",
                schema = @Schema(implementation = PagoModel.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pago no encontrado",
            content = @Content
        )
    })
    public ResponseEntity<PagoModel> patchPago(
            @Parameter(description = "ID del pago a actualizar", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Campos a actualizar")
            @RequestBody Map<String, Object> updates) {
        Pago pago = service.findById(id);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Pago.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, pago, value);
            }
        });
        Pago updatedPago = service.save(pago);
        PagoModel model = hateoasService.toModel(updatedPago);
        return ResponseEntity.ok(model);
    }
}
