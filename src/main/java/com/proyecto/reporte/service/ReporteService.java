package com.proyecto.reporte.service;

import com.proyecto.reporte.client.ClienteClient;
import com.proyecto.reporte.client.MovimientoClient;
import com.proyecto.reporte.dto.ClienteDTO;
import com.proyecto.reporte.dto.MovimientoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ReporteService {

    private final ClienteClient clienteClient;
    private final MovimientoClient movimientoClient;

    @Autowired
    public ReporteService(ClienteClient clienteClient, MovimientoClient movimientoClient) {
        this.clienteClient = clienteClient;
        this.movimientoClient = movimientoClient;
    }

    public Map<String, Object> generarReporte(Long clienteId, String fechaInicio, String fechaFin) {
        try {
            // Obtener información del cliente
            ClienteDTO cliente = clienteClient.getClienteById(clienteId);
            if (cliente == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
            }
            
            // Obtener movimientos del cliente
            List<MovimientoDTO> movimientos = movimientoClient.getMovimientosByClienteAndFecha(
                clienteId, fechaInicio, fechaFin);

            // Construir el reporte
            Map<String, Object> reporte = new HashMap<>();
            reporte.put("cliente", cliente);
            reporte.put("movimientos", movimientos);
            reporte.put("fechaInicio", fechaInicio);
            reporte.put("fechaFin", fechaFin);

            return reporte;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error al generar el reporte: " + e.getMessage());
        }
    }
} 