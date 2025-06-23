package com.perfulandia.usuarios.controller;

import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.model.UsuarioModel;
import com.perfulandia.usuarios.service.UsuarioHateoasService;
import com.perfulandia.usuarios.service.UsuarioService;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Usuario", description = "API para la gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioHateoasService usuarioHateoasService;

    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioModel.class)) })
    })
    @GetMapping
    public CollectionModel<UsuarioModel> listarUsuarios() {
        List<UsuarioModel> usuarios = usuarioService.listarUsuarios().stream()
                .map(usuarioHateoasService::toModel)
                .collect(Collectors.toList());
        return usuarioHateoasService.toCollectionModel(usuarios);
    }

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioModel> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        UsuarioModel model = usuarioHateoasService.toModel(nuevoUsuario);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> buscarUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.buscarUsuario(id);
        return ResponseEntity.ok(usuarioHateoasService.toModel(usuario));
    }

    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioHateoasService.toModel(usuarioActualizado));
    }

    @Operation(summary = "Actualizar parcialmente un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizarParcialUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        Usuario usuarioExistente = usuarioService.buscarUsuario(id);
        if (usuario.getNombre() != null) {
            usuarioExistente.setNombre(usuario.getNombre());
        }
        if (usuario.getCorreo() != null) {
            usuarioExistente.setCorreo(usuario.getCorreo());
        }
        if (usuario.getContrasena() != null) {
            usuarioExistente.setContrasena(usuario.getContrasena());
        }
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioExistente);
        return ResponseEntity.ok(usuarioHateoasService.toModel(usuarioActualizado));
    }

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
