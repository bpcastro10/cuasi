package com.proyecto.microcuentas.repository;

import com.proyecto.microcuentas.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {} 