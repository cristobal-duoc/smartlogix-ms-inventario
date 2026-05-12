package com.smartlogix.inventario.service;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de logica de negocio para la gestion de productos en inventario.
 *
 * Rol en la arquitectura en capas:
 *   Controller -> [Service] -> Repository -> Entity -> Base de datos
 *
 * El Service es el unico lugar donde reside la logica de negocio. Esto garantiza:
 *   1. El Controller no toma decisiones de negocio: solo recibe peticiones HTTP
 *      y delega al Service.
 *   2. El Repository no contiene logica: solo persiste y recupera datos.
 *   3. Si la logica cambia (ej: nueva regla de negocio para el stock),
 *      solo se modifica esta clase, sin tocar Controller ni Repository.
 *
 * Inyeccion de dependencias:
 * Spring inyecta ProductoRepository automaticamente en el constructor
 * (inyeccion por constructor, practica recomendada sobre @Autowired en campo).
 * Esto facilita los tests unitarios: se puede pasar un mock del repositorio
 * sin necesidad de levantar el contexto de Spring ni conectarse a la BD.
 *
 * @Service: registra esta clase como componente de servicio en el contexto de Spring.
 */
@Service
public class ProductoService {

    /**
     * Repositorio inyectado por Spring en el constructor.
     * La palabra final garantiza que la referencia no cambia despues de la
     * construccion del objeto (inmutabilidad de la dependencia).
     */
    private final ProductoRepository productoRepository;

    /**
     * Constructor con inyeccion de dependencia.
     * Spring detecta que el constructor requiere un ProductoRepository y lo
     * inyecta automaticamente al crear el bean ProductoService.
     *
     * @param productoRepository implementacion generada por Spring Data JPA.
     */
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Retorna todos los productos registrados en el inventario.
     * Delega directamente al repositorio sin logica adicional.
     *
     * @return lista completa de productos. Lista vacia si no hay registros.
     */
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su identificador unico.
     * Si el producto no existe, lanza RuntimeException con mensaje descriptivo.
     * Esta excepcion puede ser capturada por un @ControllerAdvice global
     * para retornar un HTTP 404 al cliente.
     *
     * @param id identificador del producto.
     * @return el producto encontrado.
     * @throws RuntimeException si no existe producto con ese id.
     */
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    /**
     * Retorna todos los productos almacenados en una bodega especifica.
     * Permite al administrador de la PYME consultar el stock por ubicacion fisica.
     *
     * @param bodega nombre de la bodega a consultar.
     * @return lista de productos en esa bodega.
     */
    public List<Producto> listarPorBodega(String bodega) {
        return productoRepository.findByBodega(bodega);
    }

    /**
     * Guarda un producto nuevo o actualiza uno existente.
     * Si el producto tiene id null, JPA realiza un INSERT.
     * Si tiene id existente, JPA realiza un UPDATE.
     *
     * @param producto objeto producto a persistir.
     * @return el producto guardado con su id asignado por la BD.
     */
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Actualiza unicamente el stock de un producto existente.
     * Caso de uso principal: sincronizacion de niveles de stock en tiempo real
     * cuando se procesa un pedido o llega mercaderia a la bodega.
     *
     * Se busca el producto primero para verificar que existe (lanza excepcion si no).
     * Luego se modifica solo el campo stock y se persiste el cambio.
     *
     * @param id         identificador del producto a actualizar.
     * @param nuevoStock nuevo nivel de stock a registrar.
     * @return el producto actualizado con el nuevo stock.
     */
    public Producto actualizarStock(Long id, Integer nuevoStock) {
        Producto producto = buscarPorId(id);
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto del inventario por su id.
     * Usado para dar de baja productos descontinuados.
     *
     * @param id identificador del producto a eliminar.
     */
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
