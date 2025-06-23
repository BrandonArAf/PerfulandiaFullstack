package com.perfulandia.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.model.UsuarioModel;
import com.perfulandia.usuarios.service.UsuarioHateoasService;
import com.perfulandia.usuarios.service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioHateoasService usuarioHateoasService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario1;
    private Usuario usuario2;
    private UsuarioModel usuarioModel1;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setId(1);
        usuario1.setNombre("Usuario Test 1");
        usuario1.setCorreo("test1@test.com");
        usuario1.setContrasena("pass1");

        usuario2 = new Usuario();
        usuario2.setId(2);
        usuario2.setNombre("Usuario Test 2");
        usuario2.setCorreo("test2@test.com");
        usuario2.setContrasena("pass2");

        usuarioModel1 = new UsuarioModel();
        usuarioModel1.setId(usuario1.getId());
        usuarioModel1.setNombre(usuario1.getNombre());
        usuarioModel1.setCorreo(usuario1.getCorreo());

        Link selfLink = linkTo(methodOn(UsuarioController.class).buscarUsuario(usuario1.getId())).withSelfRel();
        usuarioModel1.add(selfLink);
    }

    @Test
    void testListarUsuarios() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);
        List<UsuarioModel> usuarioModels = Arrays.asList(usuarioModel1, new UsuarioModel());
        CollectionModel<UsuarioModel> collectionModel = CollectionModel.of(usuarioModels,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());

        given(usuarioService.listarUsuarios()).willReturn(usuarios);
        given(usuarioHateoasService.toModel(any(Usuario.class))).willReturn(new UsuarioModel());
        given(usuarioHateoasService.toCollectionModel(any())).willReturn(collectionModel);


        mockMvc.perform(get("/api/v1/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void testCrearUsuario() throws Exception {
        given(usuarioService.crearUsuario(any(Usuario.class))).willReturn(usuario1);
        given(usuarioHateoasService.toModel(usuario1)).willReturn(usuarioModel1);

        mockMvc.perform(post("/api/v1/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(usuario1.getId()))
                .andExpect(jsonPath("$.nombre").value(usuario1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testBuscarUsuario() throws Exception {
        given(usuarioService.buscarUsuario(1)).willReturn(usuario1);
        given(usuarioHateoasService.toModel(usuario1)).willReturn(usuarioModel1);

        mockMvc.perform(get("/api/v1/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario1.getId()))
                .andExpect(jsonPath("$.nombre").value(usuario1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarUsuario() throws Exception {
        given(usuarioService.actualizarUsuario(anyInt(), any(Usuario.class))).willReturn(usuario1);
        given(usuarioHateoasService.toModel(usuario1)).willReturn(usuarioModel1);

        mockMvc.perform(put("/api/v1/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario1.getId()))
                .andExpect(jsonPath("$.nombre").value(usuario1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarParcialUsuario() throws Exception {
        Usuario partialUpdate = new Usuario();
        partialUpdate.setNombre("Nuevo Nombre");

        given(usuarioService.buscarUsuario(1)).willReturn(usuario1);
        given(usuarioService.actualizarUsuario(anyInt(), any(Usuario.class))).willReturn(usuario1);
        given(usuarioHateoasService.toModel(usuario1)).willReturn(usuarioModel1);


        mockMvc.perform(patch("/api/v1/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario1.getId()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }


    @Test
    void testEliminarUsuario() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1);

        mockMvc.perform(delete("/api/v1/usuario/1"))
                .andExpect(status().isNoContent());
    }
} 