package com.proyecto.microcuentas.service;

import com.proyecto.microcuentas.entity.Cuenta;
import com.proyecto.microcuentas.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CuentaService {
    @Autowired
    private CuentaRepository repo;

    public List<Cuenta> listar() { return repo.findAll(); }
    public Cuenta buscarPorId(String numeroCuenta) { return repo.findById(numeroCuenta).orElseThrow(); }
    public Cuenta guardar(Cuenta c) { return repo.save(c); }
    public void eliminar(String numeroCuenta) { repo.deleteById(numeroCuenta); }
} 