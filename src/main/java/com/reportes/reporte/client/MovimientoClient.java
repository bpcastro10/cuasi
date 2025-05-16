package com.reportes.reporte.client;

import com.reportes.reporte.dto.MovimientoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "microcuentas", url = "${microcuentas.url}")
public interface MovimientoClient {
    @GetMapping("/api/movimientos")
    List<MovimientoDTO> getMovimientosByClienteAndFecha(
        @RequestParam("clienteId") Long clienteId,
        @RequestParam("fechaInicio") String fechaInicio,
        @RequestParam("fechaFin") String fechaFin
    );
} 