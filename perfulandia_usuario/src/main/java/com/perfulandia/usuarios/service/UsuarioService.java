
package com.perfulandia.usuarios.service;

import com.perfulandia.usuarios.model.Usuario;
import com.perfulandia.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuario(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario actualizarUsuario(int id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombre(usuario.getNombre());
            existente.setCorreo(usuario.getCorreo());
            return usuarioRepository.save(existente);
        }
        return null;
    }

    public void eliminarUsuario(int id) {
        usuarioRepository.deleteById(id);
    }
}
