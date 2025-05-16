package com.proyecto.reporte.controller;

import com.proyecto.reporte.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reportes")
    public ResponseEntity<Map<String, Object>> generarReporte(
            @RequestParam("cliente") Long clienteId,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La fecha de inicio debe ser anterior a la fecha fin");
        }
        
        Map<String, Object> reporte = reporteService.generarReporte(
            clienteId,
            fechaInicio.toString(),
            fechaFin.toString()
        );
        
        return ResponseEntity.ok(reporte);
    }
} 