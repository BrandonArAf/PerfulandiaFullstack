package com.perfulandia.perfulandia_pedido.repository;

import com.perfulandia.perfulandia_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
