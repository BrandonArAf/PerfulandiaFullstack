package com.perfulandia.pago.service;

import com.perfulandia.pago.model.Pago;
import com.perfulandia.pago.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {
    @Autowired
    private PagoRepository repository;

    public List<Pago> findAll() {
        return repository.findAll();
    }

    public Pago findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Pago save(Pago pago) {
        return repository.save(pago);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}