
package com.perfulandia.perfulandia_producto.repository;

import com.perfulandia.perfulandia_producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
