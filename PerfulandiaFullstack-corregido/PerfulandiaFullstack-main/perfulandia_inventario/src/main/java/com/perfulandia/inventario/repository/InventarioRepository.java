package com.perfulandia.inventario.repository;

import com.perfulandia.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Inventario findByProductoId(Long productoId);
}