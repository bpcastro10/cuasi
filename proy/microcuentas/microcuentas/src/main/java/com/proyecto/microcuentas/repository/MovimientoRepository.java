package com.proyecto.microcuentas.repository;

import com.proyecto.microcuentas.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByFecha(LocalDate fecha);
} 