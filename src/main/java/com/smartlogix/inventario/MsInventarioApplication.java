package com.smartlogix.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio ms-inventario.
 *
 * @SpringBootApplication activa tres configuraciones en una sola anotacion:
 *   - @Configuration: permite definir beans de Spring en esta clase.
 *   - @EnableAutoConfiguration: Spring Boot configura automaticamente los componentes
 *     detectados en el classpath (JPA, web, datasource, etc.).
 *   - @ComponentScan: escanea todos los paquetes bajo com.smartlogix.inventario
 *     para registrar automaticamente los @Component, @Service, @Repository y @RestController.
 *
 * Este microservicio es responsable exclusivamente de la gestion de inventario
 * (productos, stock y bodegas). Sigue el principio de responsabilidad unica
 * de la arquitectura de microservicios: un servicio = un dominio de negocio.
 */
@SpringBootApplication
public class MsInventarioApplication {

    /**
     * Punto de entrada de la aplicacion Spring Boot.
     * SpringApplication.run inicializa el contexto de Spring, levanta el servidor
     * embebido (Tomcat por defecto) y deja el servicio escuchando en el puerto
     * configurado en application.properties (8081).
     *
     * @param args argumentos de linea de comandos (no requeridos para este servicio).
     */
    public static void main(String[] args) {
        SpringApplication.run(MsInventarioApplication.class, args);
    }
}
