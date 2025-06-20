package com.perfulandia.usuarios.controller;

import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable int id) {
        return usuarioService.buscarUsuario(id);
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    @PatchMapping("/{id}")
    public Usuario actualizarParcialUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
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
        return usuarioService.actualizarUsuario(id, usuarioExistente);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
    }
}
