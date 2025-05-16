package com.proyecto.microcuentas.service;

import com.proyecto.microcuentas.entity.Movimiento;
import com.proyecto.microcuentas.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovimientoService {
    @Autowired
    private MovimientoRepository movRepo;

    public Movimiento crearMovimiento(Movimiento mov) {
        // Aquí puedes agregar la lógica de saldo si lo deseas
        return movRepo.save(mov);
    }

    public List<Movimiento> reportePorFecha(LocalDate fecha) {
        return movRepo.findByFecha(fecha);
    }
} 