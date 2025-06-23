package com.perfulandia.usuarios.service;

import com.perfulandia.usuarios.controller.UsuarioController;
import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.model.UsuarioModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Servicio HATEOAS para Usuarios
 *
 * Agrega enlaces navegables a los modelos y colecciones de usuarios.
 */
@Service
public class UsuarioHateoasService {

    /**
     * Convierte una entidad Usuario a UsuarioModel con enlaces HATEOAS
     */
    public UsuarioModel toModel(Usuario usuario) {
        UsuarioModel model = new UsuarioModel(usuario);

        // Enlace a sí mismo
        model.add(linkTo(methodOn(UsuarioController.class).buscarUsuario(usuario.getId())).withSelfRel());

        // Enlace a la colección de usuarios
        model.add(linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"));

        // Enlaces de acciones disponibles
        model.add(linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(), null)).withRel("update"));
        model.add(Link.of("/api/v1/usuario/" + usuario.getId()).withRel("delete"));

        return model;
    }

    /**
     * Crea un modelo de colección con enlaces HATEOAS
     */
    public CollectionModel<UsuarioModel> toCollectionModel(List<UsuarioModel> models) {
        CollectionModel<UsuarioModel> collectionModel = CollectionModel.of(models);

        // Enlace a la colección
        collectionModel.add(linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());

        // Enlace para crear nuevo usuario
        collectionModel.add(linkTo(methodOn(UsuarioController.class).crearUsuario(null)).withRel("create"));

        return collectionModel;
    }
} 