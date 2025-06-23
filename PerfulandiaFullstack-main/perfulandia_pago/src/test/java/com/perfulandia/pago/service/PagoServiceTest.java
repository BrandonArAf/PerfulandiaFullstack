package com.perfulandia.pago.service;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PagoService
 * 
 * Estas pruebas verifican el comportamiento correcto del servicio de pagos,
 * incluyendo todas las operaciones CRUD y casos edge.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago1;
    private Pago pago2;

    @BeforeEach
    void setUp() {
        pago1 = new Pago(1L, 123L, 150.0, "TARJETA", "COMPLETADO");
        pago2 = new Pago(2L, 124L, 200.0, "EFECTIVO", "PENDIENTE");
    }

    @Test
    void findAll_ShouldReturnAllPagos() {
        // Arrange
        List<Pago> expectedPagos = Arrays.asList(pago1, pago2);
        when(pagoRepository.findAll()).thenReturn(expectedPagos);

        // Act
        List<Pago> result = pagoService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(pago1, pago2);
        verify(pagoRepository).findAll();
    }

    @Test
    void findById_WhenPagoExists_ShouldReturnPago() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago1));

        // Act
        Pago result = pagoService.findById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPedidoId()).isEqualTo(123L);
        assertThat(result.getMonto()).isEqualTo(150.0);
        assertThat(result.getMetodo()).isEqualTo("TARJETA");
        assertThat(result.getEstado()).isEqualTo("COMPLETADO");
        verify(pagoRepository).findById(1L);
    }

    @Test
    void findById_WhenPagoDoesNotExist_ShouldReturnNull() {
        // Arrange
        when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Pago result = pagoService.findById(999L);

        // Assert
        assertThat(result).isNull();
        verify(pagoRepository).findById(999L);
    }

    @Test
    void save_ShouldSaveAndReturnPago() {
        // Arrange
        Pago newPago = new Pago(null, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        Pago savedPago = new Pago(3L, 125L, 175.0, "TRANSFERENCIA", "PENDIENTE");
        when(pagoRepository.save(any(Pago.class))).thenReturn(savedPago);

        // Act
        Pago result = pagoService.save(newPago);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getPedidoId()).isEqualTo(125L);
        assertThat(result.getMonto()).isEqualTo(175.0);
        assertThat(result.getMetodo()).isEqualTo("TRANSFERENCIA");
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        verify(pagoRepository).save(newPago);
    }

    @Test
    void save_WhenUpdatingExistingPago_ShouldUpdateAndReturnPago() {
        // Arrange
        Pago existingPago = new Pago(1L, 123L, 150.0, "TARJETA", "COMPLETADO");
        Pago updatedPago = new Pago(1L, 123L, 180.0, "TARJETA", "PROCESANDO");
        when(pagoRepository.save(any(Pago.class))).thenReturn(updatedPago);

        // Act
        Pago result = pagoService.save(existingPago);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMonto()).isEqualTo(180.0);
        assertThat(result.getEstado()).isEqualTo("PROCESANDO");
        verify(pagoRepository).save(existingPago);
    }

    @Test
    void deleteById_ShouldDeletePago() {
        // Arrange
        doNothing().when(pagoRepository).deleteById(1L);

        // Act
        pagoService.deleteById(1L);

        // Assert
        verify(pagoRepository).deleteById(1L);
    }

    @Test
    void save_WithNullValues_ShouldHandleGracefully() {
        // Arrange
        Pago pagoWithNulls = new Pago();
        pagoWithNulls.setPedidoId(null);
        pagoWithNulls.setMonto(null);
        pagoWithNulls.setMetodo(null);
        pagoWithNulls.setEstado(null);

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoWithNulls);

        // Act
        Pago result = pagoService.save(pagoWithNulls);

        // Assert
        assertThat(result).isNotNull();
        verify(pagoRepository).save(pagoWithNulls);
    }

    @Test
    void findAll_WhenNoPagosExist_ShouldReturnEmptyList() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Pago> result = pagoService.findAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pagoRepository).findAll();
    }

    @Test
    void save_WithLargeAmount_ShouldHandleCorrectly() {
        // Arrange
        Pago largeAmountPago = new Pago(null, 126L, 999999.99, "TARJETA", "PENDIENTE");
        Pago savedLargeAmountPago = new Pago(4L, 126L, 999999.99, "TARJETA", "PENDIENTE");
        when(pagoRepository.save(any(Pago.class))).thenReturn(savedLargeAmountPago);

        // Act
        Pago result = pagoService.save(largeAmountPago);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMonto()).isEqualTo(999999.99);
        verify(pagoRepository).save(largeAmountPago);
    }

    @Test
    void save_WithSpecialCharactersInMetodo_ShouldHandleCorrectly() {
        // Arrange
        Pago specialMetodoPago = new Pago(null, 127L, 100.0, "TARJETA_CRÉDITO", "PENDIENTE");
        Pago savedSpecialMetodoPago = new Pago(5L, 127L, 100.0, "TARJETA_CRÉDITO", "PENDIENTE");
        when(pagoRepository.save(any(Pago.class))).thenReturn(savedSpecialMetodoPago);

        // Act
        Pago result = pagoService.save(specialMetodoPago);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMetodo()).isEqualTo("TARJETA_CRÉDITO");
        verify(pagoRepository).save(specialMetodoPago);
    }
} 