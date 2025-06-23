package com.perfulandia.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.model.InventarioModel;
import com.perfulandia.inventario.service.InventarioHateoasService;
import com.perfulandia.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Pruebas unitarias para el controlador de inventario.
 * Esta clase contiene pruebas que verifican el comportamiento correcto de todos los endpoints
 * del controlador de inventario, incluyendo casos exitosos y casos de error.
 * 
 * Las pruebas utilizan:
 * - MockMvc para simular peticiones HTTP
 * - Mockito para simular dependencias (servicios)
 * - JUnit 5 para la estructura de pruebas
 * - Assertions para verificar resultados esperados
 */
@WebMvcTest(InventarioController.class) // Anotación que configura el contexto de prueba solo para el controlador
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Cliente HTTP simulado para hacer peticiones a los endpoints

    @MockBean
    private InventarioService inventarioService; // Servicio simulado para evitar dependencias reales

    @MockBean
    private InventarioHateoasService inventarioHateoasService; // Servicio HATEOAS simulado

    @Autowired
    private ObjectMapper objectMapper; // Utilidad para convertir objetos a JSON y viceversa

    private Inventario inventario1; // Datos de prueba para el primer inventario
    private Inventario inventario2; // Datos de prueba para el segundo inventario
    private InventarioModel inventarioModel1; // Modelo HATEOAS para el primer inventario

    /**
     * Configuración inicial que se ejecuta antes de cada prueba.
     * Este método crea los datos de prueba que se usarán en todas las pruebas.
     */
    @BeforeEach
    void setUp() {
        // Crea datos de prueba para inventarios
        inventario1 = new Inventario(1L, 1L, 100, "Almacén A");
        inventario2 = new Inventario(2L, 2L, 50, "Almacén B");
        inventarioModel1 = new InventarioModel(inventario1);

        // Agrega un enlace HATEOAS de ejemplo al modelo
        Link selfLink = linkTo(methodOn(InventarioController.class).getByProductoId(inventario1.getProductoId())).withSelfRel();
        inventarioModel1.add(selfLink);
    }

    /**
     * Prueba el endpoint GET /api/v1/inventario para obtener todos los inventarios.
     * Verifica que el endpoint retorna una lista completa de inventarios con enlaces HATEOAS.
     */
    @Test
    void testGetAll() throws Exception {
        // Arrange: Prepara los datos de prueba
        List<Inventario> inventarios = Arrays.asList(inventario1, inventario2);
        List<InventarioModel> inventarioModels = Arrays.asList(inventarioModel1, new InventarioModel(inventario2));
        CollectionModel<InventarioModel> collectionModel = CollectionModel.of(inventarioModels,
                linkTo(methodOn(InventarioController.class).getAll()).withSelfRel());

        // Configura el comportamiento esperado de los servicios simulados
        given(inventarioService.findAll()).willReturn(inventarios);
        given(inventarioHateoasService.toModel(any(Inventario.class))).willReturn(new InventarioModel(new Inventario()));
        given(inventarioHateoasService.toCollectionModel(any())).willReturn(collectionModel);

        // Act & Assert: Ejecuta la petición y verifica los resultados
        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk()) // Verifica que el código de respuesta sea 200
                .andExpect(jsonPath("$._links.self.href").exists()) // Verifica que exista el enlace "self"
                .andExpect(jsonPath("$._embedded").exists()); // Verifica que exista la sección "_embedded"
    }

    /**
     * Prueba el endpoint GET /api/v1/inventario/{productoId} para obtener un inventario específico.
     * Verifica que el endpoint retorna el inventario correcto cuando existe.
     */
    @Test
    void testGetByProductoId() throws Exception {
        // Arrange: Configura el comportamiento esperado
        given(inventarioService.findByProductoId(1L)).willReturn(inventario1);
        given(inventarioHateoasService.toModel(inventario1)).willReturn(inventarioModel1);

        // Act & Assert: Ejecuta la petición y verifica los resultados
        mockMvc.perform(get("/api/v1/inventario/1"))
                .andExpect(status().isOk()) // Verifica código 200
                .andExpect(jsonPath("$.id").value(inventario1.getId())) // Verifica el ID
                .andExpect(jsonPath("$.productoId").value(inventario1.getProductoId())) // Verifica el ID del producto
                .andExpect(jsonPath("$._links.self.href").exists()); // Verifica enlaces HATEOAS
    }

    /**
     * Prueba el endpoint GET /api/v1/inventario/{productoId} cuando el inventario no existe.
     * Verifica que el endpoint retorna código 404 cuando no se encuentra el inventario.
     */
    @Test
    void testGetByProductoId_NotFound() throws Exception {
        // Arrange: Configura el servicio para lanzar una excepción
        given(inventarioService.findByProductoId(999L)).willThrow(new RuntimeException("Inventario no encontrado"));

        // Act & Assert: Verifica que se retorna código 404
        mockMvc.perform(get("/api/v1/inventario/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Prueba el endpoint POST /api/v1/inventario para crear un nuevo inventario.
     * Verifica que el endpoint crea correctamente un nuevo registro y retorna código 201.
     */
    @Test
    void testCreate() throws Exception {
        // Arrange: Configura el comportamiento esperado
        given(inventarioService.save(any(Inventario.class))).willReturn(inventario1);
        given(inventarioHateoasService.toModel(inventario1)).willReturn(inventarioModel1);

        // Act & Assert: Ejecuta la petición POST y verifica los resultados
        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON) // Especifica el tipo de contenido
                        .content(objectMapper.writeValueAsString(inventario1))) // Convierte el objeto a JSON
                .andExpect(status().isCreated()) // Verifica código 201 (Created)
                .andExpect(header().exists("Location")) // Verifica que existe el header Location
                .andExpect(jsonPath("$.id").value(inventario1.getId())) // Verifica el ID
                .andExpect(jsonPath("$.productoId").value(inventario1.getProductoId())) // Verifica el ID del producto
                .andExpect(jsonPath("$._links.self.href").exists()); // Verifica enlaces HATEOAS
    }

    /**
     * Prueba el endpoint PUT /api/v1/inventario/{productoId}/aumentar para aumentar stock.
     * Verifica que el endpoint aumenta correctamente la cantidad disponible.
     */
    @Test
    void testAumentar() throws Exception {
        // Arrange: Prepara datos de prueba para inventario aumentado
        Inventario inventarioAumentado = new Inventario(1L, 1L, 150, "Almacén A");
        given(inventarioService.ajustarCantidad(1L, 50)).willReturn(inventarioAumentado);
        given(inventarioHateoasService.toModel(inventarioAumentado)).willReturn(new InventarioModel(inventarioAumentado));

        // Act & Assert: Ejecuta la petición PUT y verifica los resultados
        mockMvc.perform(put("/api/v1/inventario/1/aumentar")
                        .param("cantidad", "50")) // Agrega parámetro de query
                .andExpect(status().isOk()) // Verifica código 200
                .andExpect(jsonPath("$.cantidadDisponible").value(150)); // Verifica la cantidad aumentada
    }

    /**
     * Prueba el endpoint PUT /api/v1/inventario/{productoId}/reducir para reducir stock.
     * Verifica que el endpoint reduce correctamente la cantidad disponible.
     */
    @Test
    void testReducir() throws Exception {
        // Arrange: Prepara datos de prueba para inventario reducido
        Inventario inventarioReducido = new Inventario(1L, 1L, 50, "Almacén A");
        given(inventarioService.ajustarCantidad(1L, -50)).willReturn(inventarioReducido);
        given(inventarioHateoasService.toModel(inventarioReducido)).willReturn(new InventarioModel(inventarioReducido));

        // Act & Assert: Ejecuta la petición PUT y verifica los resultados
        mockMvc.perform(put("/api/v1/inventario/1/reducir")
                        .param("cantidad", "50")) // Agrega parámetro de query
                .andExpect(status().isOk()) // Verifica código 200
                .andExpect(jsonPath("$.cantidadDisponible").value(50)); // Verifica la cantidad reducida
    }

    /**
     * Prueba el endpoint PUT /api/v1/inventario/{productoId} para actualizar inventario.
     * Verifica que el endpoint actualiza correctamente todos los campos del inventario.
     */
    @Test
    void testUpdate() throws Exception {
        // Arrange: Prepara datos de prueba para inventario actualizado
        Inventario inventarioActualizado = new Inventario(1L, 1L, 200, "Almacén C");
        given(inventarioService.findByProductoId(1L)).willReturn(inventario1);
        given(inventarioService.save(any(Inventario.class))).willReturn(inventarioActualizado);
        given(inventarioHateoasService.toModel(inventarioActualizado)).willReturn(new InventarioModel(inventarioActualizado));

        // Act & Assert: Ejecuta la petición PUT y verifica los resultados
        mockMvc.perform(put("/api/v1/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioActualizado)))
                .andExpect(status().isOk()) // Verifica código 200
                .andExpect(jsonPath("$.cantidadDisponible").value(200)) // Verifica cantidad actualizada
                .andExpect(jsonPath("$.ubicacion").value("Almacén C")); // Verifica ubicación actualizada
    }

    /**
     * Prueba el endpoint PATCH /api/v1/inventario/{id} para actualización parcial.
     * Verifica que el endpoint actualiza correctamente solo los campos especificados.
     */
    @Test
    void testPatchInventario() throws Exception {
        // Arrange: Prepara datos de prueba para actualización parcial
        Inventario inventarioPatch = new Inventario(1L, 1L, 75, "Almacén A");
        given(inventarioService.findById(1L)).willReturn(inventario1);
        given(inventarioService.save(any(Inventario.class))).willReturn(inventarioPatch);
        given(inventarioHateoasService.toModel(inventarioPatch)).willReturn(new InventarioModel(inventarioPatch));

        String patchData = "{\"cantidadDisponible\": 75}"; // Datos JSON para actualización parcial

        // Act & Assert: Ejecuta la petición PATCH y verifica los resultados
        mockMvc.perform(patch("/api/v1/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchData))
                .andExpect(status().isOk()) // Verifica código 200
                .andExpect(jsonPath("$.cantidadDisponible").value(75)); // Verifica campo actualizado
    }

    /**
     * Prueba el endpoint DELETE /api/v1/inventario/{id} para eliminar inventario.
     * Verifica que el endpoint elimina correctamente el inventario y retorna código 204.
     */
    @Test
    void testDelete() throws Exception {
        // Arrange: Configura el comportamiento esperado
        given(inventarioService.findById(1L)).willReturn(inventario1);
        doNothing().when(inventarioService).deleteById(1L);

        // Act & Assert: Ejecuta la petición DELETE y verifica los resultados
        mockMvc.perform(delete("/api/v1/inventario/1"))
                .andExpect(status().isNoContent()); // Verifica código 204 (No Content)
    }

    /**
     * Prueba el endpoint DELETE /api/v1/inventario/{id} cuando el inventario no existe.
     * Verifica que el endpoint retorna código 404 cuando no se encuentra el inventario.
     */
    @Test
    void testDelete_NotFound() throws Exception {
        // Arrange: Configura el servicio para lanzar una excepción
        given(inventarioService.findById(999L)).willThrow(new RuntimeException("Inventario no encontrado"));

        // Act & Assert: Verifica que se retorna código 404
        mockMvc.perform(delete("/api/v1/inventario/999"))
                .andExpect(status().isNotFound());
    }
} 