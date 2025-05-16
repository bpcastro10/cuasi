package com.reportes.reporte.client;

import com.reportes.reporte.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microclientes", url = "${microclientes.url}")
public interface ClienteClient {
    @GetMapping("/api/clientes/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);
} 