package com.perfulandia.pago.service;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Pago guardar(Pago pago) {
        return pagoRepository.save(pago);
    }

    public Pago obtenerPorId(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago actualizar(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void eliminar(Long id) {
        pagoRepository.deleteById(id);
    }
}
