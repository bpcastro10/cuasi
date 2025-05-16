package com.proyecto.microcuentas.controller;

import com.proyecto.microcuentas.entity.Cuenta;
import com.proyecto.microcuentas.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {
    @Autowired
    private CuentaRepository cuentaRepository;

    @GetMapping
    public List<Cuenta> listar() {
        return cuentaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Cuenta obtener(@PathVariable String id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Cuenta crear(@RequestBody Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @PutMapping("/{id}")
    public Cuenta actualizar(@PathVariable String id, @RequestBody Cuenta cuenta) {
        cuenta.setNumeroCuenta(id);
        return cuentaRepository.save(cuenta);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        cuentaRepository.deleteById(id);
    }
} 