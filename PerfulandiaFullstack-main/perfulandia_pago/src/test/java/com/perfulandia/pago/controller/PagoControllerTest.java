package com.perfulandia.pago.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.model.PagoModel;
import com.perfulandia.pago.service.PagoHateoasService;
import com.perfulandia.pago.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para PagoController
 * 
 * Estas pruebas verifican el comportamiento correcto de todos los endpoints
 * del controlador de pagos, incluyendo casos de éxito y error.
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @Mock
    private PagoHateoasService hateoasService;

    @InjectMocks
    private PagoController pagoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Pago pago1;
    private Pago pago2;
    private PagoModel pagoModel1;
    private PagoModel pagoModel2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
        objectMapper = new ObjectMapper();

        // Configurar datos de prueba
        pago1 = new Pago(1L, 123L, 150.0, "TARJETA", "COMPLETADO");
        pago2 = new Pago(2L, 124L, 200.0, "EFECTIVO", "PENDIENTE");

        pagoModel1 = new PagoModel(pago1);
        pagoModel2 = new PagoModel(pago2);
    }

    @Test
    void getAll_ShouldReturnAllPagos() throws Exception {
        // Arrange
        List<Pago> pagos = Arrays.asList(pago1, pago2);
        List<PagoModel> pagoModels = Arrays.asList(pagoModel1, pagoModel2);
        CollectionModel<PagoModel> collectionModel = CollectionModel.of(pagoModels);

        when(pagoService.findAll()).thenReturn(pagos);
        when(hateoasService.toCollectionModel(pagos)).thenReturn(collectionModel);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pago"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(pagoService).findAll();
        verify(hateoasService).toCollectionModel(pagos);
    }

    @Test
    void getById_WhenPagoExists_ShouldReturnPago() throws Exception {
        // Arrange
        when(pagoService.findById(1L)).thenReturn(pago1);
        when(hateoasService.toModel(pago1)).thenReturn(pagoModel1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pago/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(pagoService).findById(1L);
        verify(hateoasService).toModel(pago1);
    }

    @Test
    void getById_WhenPagoDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(pagoService.findById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pago/999"))
                .andExpect(status().isNotFound());

        verify(pagoService).findById(999L);
        verify(hateoasService, never()).toModel(any());
    }

    @Test
    void create_ShouldCreateAndReturnPago() throws Exception {
        // Arrange
        Pago newPago = new Pago(null, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        Pago savedPago = new Pago(3L, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        PagoModel savedPagoModel = new PagoModel(savedPago);

        when(pagoService.save(any(Pago.class))).thenReturn(savedPago);
        when(hateoasService.toModel(savedPago)).thenReturn(savedPagoModel);

        // Act & Assert
        mockMvc.perform(post("/api/v1/pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPago)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(pagoService).save(any(Pago.class));
        verify(hateoasService).toModel(savedPago);
    }

    @Test
    void update_WhenPagoExists_ShouldUpdateAndReturnPago() throws Exception {
        // Arrange
        Pago updateData = new Pago(null, 123L, 180.0, "TARJETA", "COMPLETADO");
        Pago updatedPago = new Pago(1L, 123L, 180.0, "TARJETA", "COMPLETADO");
        PagoModel updatedPagoModel = new PagoModel(updatedPago);

        when(pagoService.findById(1L)).thenReturn(pago1);
        when(pagoService.save(any(Pago.class))).thenReturn(updatedPago);
        when(hateoasService.toModel(updatedPago)).thenReturn(updatedPagoModel);

        // Act & Assert
        mockMvc.perform(put("/api/v1/pago/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(pagoService).findById(1L);
        verify(pagoService).save(any(Pago.class));
        verify(hateoasService).toModel(updatedPago);
    }

    @Test
    void update_WhenPagoDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        Pago updateData = new Pago(null, 123L, 180.0, "TARJETA", "COMPLETADO");
        when(pagoService.findById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/v1/pago/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(pagoService).findById(999L);
        verify(pagoService, never()).save(any());
        verify(hateoasService, never()).toModel(any());
    }

    @Test
    void delete_WhenPagoExists_ShouldDeleteAndReturnNoContent() throws Exception {
        // Arrange
        when(pagoService.findById(1L)).thenReturn(pago1);
        doNothing().when(pagoService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/pago/1"))
                .andExpect(status().isNoContent());

        verify(pagoService).findById(1L);
        verify(pagoService).deleteById(1L);
    }

    @Test
    void delete_WhenPagoDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(pagoService.findById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/pago/999"))
                .andExpect(status().isNotFound());

        verify(pagoService).findById(999L);
        verify(pagoService, never()).deleteById(any());
    }

    @Test
    void patchPago_WhenPagoExists_ShouldPatchAndReturnPago() throws Exception {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("monto", 160.0);
        updates.put("estado", "PROCESANDO");

        Pago patchedPago = new Pago(1L, 123L, 160.0, "TARJETA", "PROCESANDO");
        PagoModel patchedPagoModel = new PagoModel(patchedPago);

        when(pagoService.findById(1L)).thenReturn(pago1);
        when(pagoService.save(any(Pago.class))).thenReturn(patchedPago);
        when(hateoasService.toModel(patchedPago)).thenReturn(patchedPagoModel);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pago/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(pagoService).findById(1L);
        verify(pagoService).save(any(Pago.class));
        verify(hateoasService).toModel(patchedPago);
    }

    @Test
    void patchPago_WhenPagoDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("monto", 160.0);
        when(pagoService.findById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pago/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());

        verify(pagoService).findById(999L);
        verify(pagoService, never()).save(any());
        verify(hateoasService, never()).toModel(any());
    }

    // Pruebas adicionales para validar el comportamiento del servicio
    @Test
    void testServiceIntegration() {
        // Arrange
        List<Pago> pagos = Arrays.asList(pago1, pago2);
        List<PagoModel> pagoModels = Arrays.asList(pagoModel1, pagoModel2);
        CollectionModel<PagoModel> collectionModel = CollectionModel.of(pagoModels);

        when(pagoService.findAll()).thenReturn(pagos);
        when(hateoasService.toCollectionModel(pagos)).thenReturn(collectionModel);

        // Act
        ResponseEntity<CollectionModel<PagoModel>> response = pagoController.getAll();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);

        verify(pagoService).findAll();
        verify(hateoasService).toCollectionModel(pagos);
    }

    @Test
    void testCreatePagoIntegration() {
        // Arrange
        Pago newPago = new Pago(null, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        Pago savedPago = new Pago(3L, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        PagoModel savedPagoModel = new PagoModel(savedPago);

        when(pagoService.save(any(Pago.class))).thenReturn(savedPago);
        when(hateoasService.toModel(savedPago)).thenReturn(savedPagoModel);

        // Act
        ResponseEntity<PagoModel> response = pagoController.create(newPago);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(3L);

        verify(pagoService).save(any(Pago.class));
        verify(hateoasService).toModel(savedPago);
    }
} 