package com.smartlogix.inventario.repository;

import com.smartlogix.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Producto.
 *
 * PATRON APLICADO: Repository Pattern.
 * Este patron actua como una capa de abstraccion entre la logica de negocio
 * (ProductoService) y la capa de persistencia (base de datos PostgreSQL).
 * El Service no sabe si los datos vienen de PostgreSQL, MySQL u otra fuente:
 * solo interactua con esta interfaz.
 *
 * Al extender JpaRepository<Producto, Long>, Spring Data JPA genera
 * automaticamente en tiempo de ejecucion la implementacion de los metodos
 * CRUD basicos sin necesidad de escribir SQL:
 *   - findAll()       -> SELECT * FROM productos
 *   - findById(id)    -> SELECT * FROM productos WHERE id = ?
 *   - save(producto)  -> INSERT o UPDATE segun si el objeto tiene ID o no
 *   - deleteById(id)  -> DELETE FROM productos WHERE id = ?
 *
 * Los metodos personalizados usan el mecanismo de "query derivada" de Spring Data:
 * Spring interpreta el nombre del metodo y genera el SQL correspondiente
 * automaticamente. No se requiere @Query ni SQL manual.
 *
 * @Repository: marca esta interfaz como componente de acceso a datos de Spring.
 * Tambien activa la traduccion de excepciones de persistencia a excepciones de Spring.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca todos los productos almacenados en una bodega especifica.
     * Spring Data genera: SELECT * FROM productos WHERE bodega = ?
     * Permite sincronizar el stock entre multiples bodegas y tiendas (caso de uso S1).
     *
     * @param bodega nombre de la bodega a filtrar.
     * @return lista de productos en esa bodega. Lista vacia si no hay resultados.
     */
    List<Producto> findByBodega(String bodega);

    /**
     * Busca productos cuyo stock supere un minimo indicado.
     * Spring Data genera: SELECT * FROM productos WHERE stock > ?
     * Util para alertas de reposicion y reportes de disponibilidad.
     *
     * @param minStock umbral minimo de stock (exclusivo).
     * @return lista de productos con stock mayor al umbral.
     */
    List<Producto> findByStockGreaterThan(Integer minStock);

    /**
     * Busca productos cuyo nombre contenga el texto indicado, sin importar mayusculas.
     * Spring Data genera: SELECT * FROM productos WHERE LOWER(nombre) LIKE LOWER('%?%')
     * Permite busquedas parciales desde el frontend (ej: buscar "camisa" encuentra "Camiseta").
     *
     * @param nombre texto parcial a buscar en el nombre del producto.
     * @return lista de productos que coinciden con la busqueda.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
