package com.proyecto.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.proyecto.reporte.client")
public class ReporteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReporteApplication.class, args);
    }
} 