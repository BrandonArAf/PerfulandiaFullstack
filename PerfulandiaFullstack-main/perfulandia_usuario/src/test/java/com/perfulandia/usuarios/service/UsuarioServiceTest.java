package com.perfulandia.usuarios.service;

import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@user.com");
        usuario.setContrasena("password");
    }

    @Test
    void testListarUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2);
        given(usuarioRepository.findAll()).willReturn(Arrays.asList(usuario, usuario2));

        // Act
        List<Usuario> result = usuarioService.listarUsuarios();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testCrearUsuario() {
        // Arrange
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuario);

        // Act
        Usuario result = usuarioService.crearUsuario(new Usuario());

        // Assert
        assertNotNull(result);
        assertEquals(usuario.getNombre(), result.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testBuscarUsuario_Success() {
        // Arrange
        given(usuarioRepository.findById(1)).willReturn(Optional.of(usuario));

        // Act
        Usuario result = usuarioService.buscarUsuario(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(usuarioRepository).findById(1);
    }

    @Test
    void testBuscarUsuario_NotFound() {
        // Arrange
        given(usuarioRepository.findById(1)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarUsuario(1);
        });
        verify(usuarioRepository).findById(1);
    }

    @Test
    void testActualizarUsuario_Success() {
        // Arrange
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre("Updated User");

        given(usuarioRepository.existsById(1)).willReturn(true);
        given(usuarioRepository.save(any(Usuario.class))).willReturn(usuarioActualizado);

        // Act
        Usuario result = usuarioService.actualizarUsuario(1, usuarioActualizado);

        // Assert
        assertNotNull(result);
        assertEquals("Updated User", result.getNombre());
        verify(usuarioRepository).existsById(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_NotFound() {
        // Arrange
        given(usuarioRepository.existsById(1)).willReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(1, new Usuario());
        });
        verify(usuarioRepository).existsById(1);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_Success() {
        // Arrange
        given(usuarioRepository.existsById(1)).willReturn(true);
        doNothing().when(usuarioRepository).deleteById(1);

        // Act
        usuarioService.eliminarUsuario(1);

        // Assert
        verify(usuarioRepository).existsById(1);
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void testEliminarUsuario_NotFound() {
        // Arrange
        given(usuarioRepository.existsById(1)).willReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(1);
        });
        verify(usuarioRepository).existsById(1);
        verify(usuarioRepository, never()).deleteById(anyInt());
    }
} 