package com.proyecto.microcuentas.controller;

import com.proyecto.microcuentas.dto.MovimientoDTO;
import com.proyecto.microcuentas.dto.CuentaDTO;
import com.proyecto.microcuentas.entity.Movimiento;
import com.proyecto.microcuentas.entity.Cuenta;
import com.proyecto.microcuentas.service.MovimientoService;
import com.proyecto.microcuentas.repository.MovimientoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {
    @Autowired
    private MovimientoService service;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private MovimientoRepository movimientoRepository;

    @PostMapping
    public MovimientoDTO crear(@RequestBody MovimientoDTO dto) {
        Movimiento m = toEntity(dto);
        return toDTO(service.crearMovimiento(m));
    }

    @GetMapping("/reportes")
    public List<MovimientoDTO> reporte(@RequestParam LocalDate fecha) {
        return service.reportePorFecha(fecha).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @GetMapping
    public List<Movimiento> listar() {
        return movimientoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Movimiento obtener(@PathVariable Long id) {
        return movimientoRepository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public Movimiento actualizar(@PathVariable Long id, @RequestBody Movimiento movimiento) {
        movimiento.setId(id);
        return movimientoRepository.save(movimiento);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        movimientoRepository.deleteById(id);
    }

    private MovimientoDTO toDTO(Movimiento m) {
        MovimientoDTO dto = mapper.map(m, MovimientoDTO.class);
        if (m.getCuenta() != null) {
            dto.setCuenta(mapper.map(m.getCuenta(), CuentaDTO.class));
        }
        return dto;
    }

    private Movimiento toEntity(MovimientoDTO dto) {
        Movimiento m = mapper.map(dto, Movimiento.class);
        if (dto.getCuenta() != null) {
            m.setCuenta(mapper.map(dto.getCuenta(), Cuenta.class));
        }
        return m;
    }
} 