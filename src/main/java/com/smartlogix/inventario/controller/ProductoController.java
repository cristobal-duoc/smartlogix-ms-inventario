package com.smartlogix.inventario.controller;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestion de productos del inventario.
 *
 * Rol en la arquitectura en capas:
 *   [Controller] -> Service -> Repository -> Entity -> Base de datos
 *
 * El Controller es el punto de entrada HTTP del microservicio. Su unica
 * responsabilidad es:
 *   1. Recibir peticiones HTTP del BFF o del API Gateway.
 *   2. Delegar la logica al ProductoService.
 *   3. Retornar la respuesta HTTP apropiada al cliente.
 *
 * El Controller NO contiene logica de negocio. Solo traduce HTTP <-> Java.
 *
 * @RestController: combinacion de @Controller + @ResponseBody. Indica que
 * todos los metodos retornan datos serializados como JSON directamente en
 * el cuerpo de la respuesta HTTP (no vistas HTML).
 *
 * @RequestMapping("/inventario/productos"): prefijo base para todos los
 * endpoints de este controlador. Todos los endpoints empiezan con esta ruta.
 *
 * @CrossOrigin(origins = "*"): permite peticiones desde cualquier origen
 * (CORS). Necesario para que el frontend React pueda consumir este servicio
 * durante el desarrollo. En produccion se restringiria al dominio del frontend.
 */
@RestController
@RequestMapping("/inventario/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    /**
     * Servicio inyectado por Spring. El Controller depende del Service,
     * no del Repository directamente. Esto respeta la arquitectura en capas:
     * cada capa solo conoce a la capa inmediatamente inferior.
     */
    private final ProductoService productoService;

    /**
     * Inyeccion por constructor. Spring provee automaticamente el bean
     * ProductoService registrado con @Service.
     *
     * @param productoService servicio de logica de negocio de inventario.
     */
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Lista todos los productos del inventario.
     *
     * GET /inventario/productos
     * Respuesta: 200 OK + lista de productos en JSON.
     * Usado por el frontend para mostrar el panel de inventario completo.
     *
     * @return lista de todos los productos registrados.
     */
    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    /**
     * Obtiene un producto especifico por su ID.
     *
     * GET /inventario/productos/{id}
     * Respuesta: 200 OK + producto en JSON, o 500 si no existe (mejorable con @ControllerAdvice).
     *
     * @PathVariable: extrae el valor {id} de la URL y lo convierte a Long.
     *
     * @param id identificador del producto.
     * @return ResponseEntity con el producto encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    /**
     * Lista productos filtrados por bodega.
     *
     * GET /inventario/productos/bodega/{bodega}
     * Respuesta: 200 OK + lista de productos de esa bodega.
     * Permite al administrador consultar el stock por ubicacion fisica.
     *
     * @param bodega nombre de la bodega a filtrar.
     * @return lista de productos en la bodega indicada.
     */
    @GetMapping("/bodega/{bodega}")
    public List<Producto> listarPorBodega(@PathVariable String bodega) {
        return productoService.listarPorBodega(bodega);
    }

    /**
     * Crea un nuevo producto en el inventario.
     *
     * POST /inventario/productos
     * Body: JSON con los datos del producto (nombre, stock, bodega, precio).
     * Respuesta: 200 OK + producto creado con su ID asignado por PostgreSQL.
     *
     * @RequestBody: deserializa el JSON del body HTTP al objeto Producto
     * usando Jackson (libreria de serializacion incluida en Spring Boot Web).
     *
     * @param producto datos del nuevo producto recibidos en el body.
     * @return el producto guardado con su id generado.
     */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    /**
     * Actualiza el nivel de stock de un producto existente.
     *
     * PUT /inventario/productos/{id}/stock?nuevoStock=50
     * Respuesta: 200 OK + producto con stock actualizado.
     * Endpoint principal para sincronizacion de inventario en tiempo real:
     * cuando ms-pedidos procesa un pedido, llama a este endpoint para
     * descontar las unidades vendidas del stock.
     *
     * @RequestParam: extrae el parametro "nuevoStock" del query string de la URL.
     *
     * @param id         identificador del producto.
     * @param nuevoStock nuevo valor de stock a registrar.
     * @return el producto con el stock actualizado.
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer nuevoStock) {
        return ResponseEntity.ok(productoService.actualizarStock(id, nuevoStock));
    }

    /**
     * Elimina un producto del inventario.
     *
     * DELETE /inventario/productos/{id}
     * Respuesta: 204 No Content (exito sin cuerpo de respuesta).
     * HTTP 204 es el codigo estandar para operaciones DELETE exitosas.
     *
     * @param id identificador del producto a eliminar.
     * @return ResponseEntity vacio con status 204.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
