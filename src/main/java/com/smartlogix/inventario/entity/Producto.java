package com.smartlogix.inventario.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un producto en el inventario de SmartLogix.
 *
 * JPA (Java Persistence API) es la especificacion de Java para mapear objetos
 * Java a tablas de base de datos relacional (ORM - Object Relational Mapping).
 * Spring Data JPA utiliza Hibernate como implementacion de JPA por defecto.
 *
 * Esta clase define el esquema de la tabla "productos" en inventario_db.
 * Hibernate crea o actualiza la tabla automaticamente gracias a la configuracion
 * spring.jpa.hibernate.ddl-auto=update en application.properties.
 *
 * Rol en la arquitectura en capas:
 *   Controller -> Service -> Repository -> [Entity] -> Base de datos
 * La Entity es el objeto que el Repository persiste y recupera de la BD.
 */
@Entity  // Indica a JPA que esta clase es una entidad persistente (se mapea a una tabla).
@Table(name = "productos")  // Especifica el nombre exacto de la tabla en PostgreSQL.
public class Producto {

    /**
     * Identificador unico del producto.
     *
     * @Id: marca este campo como clave primaria de la tabla.
     * @GeneratedValue(strategy = GenerationType.IDENTITY): delega la generacion
     * del ID a PostgreSQL usando su mecanismo SERIAL (autoincremento). Cada vez
     * que se inserta un producto, la BD asigna el siguiente ID disponible.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo del producto (ej: "Camiseta Talla M").
     * nullable = false: genera una restriccion NOT NULL en la columna de PostgreSQL,
     * garantizando que ningun producto pueda guardarse sin nombre.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Cantidad de unidades disponibles en el inventario.
     * Representa el nivel de stock actual del producto en una bodega especifica.
     * nullable = false: el stock siempre debe estar definido (puede ser 0, no null).
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * Identificador o nombre de la bodega donde se encuentra el producto.
     * Permite filtrar productos por ubicacion fisica (ej: "Bodega Norte", "Bodega Sur").
     * Clave para el caso de uso de sincronizacion entre multiples bodegas y tiendas.
     */
    @Column(nullable = false)
    private String bodega;

    /**
     * Precio unitario del producto en pesos chilenos.
     * Se permite null para productos cuyo precio aun no ha sido definido.
     */
    @Column
    private Double precio;

    /**
     * Constructor vacio requerido por JPA.
     * JPA necesita instanciar la entidad mediante reflexion cuando recupera
     * registros desde la base de datos. Sin este constructor, Hibernate lanza
     * una excepcion al intentar reconstruir objetos desde la BD.
     */
    public Producto() {}

    /**
     * Constructor de conveniencia para crear productos con todos sus campos.
     * Usado principalmente en tests y en datos de inicializacion.
     *
     * @param nombre nombre del producto.
     * @param stock  cantidad inicial en inventario.
     * @param bodega bodega donde se almacena el producto.
     * @param precio precio unitario del producto.
     */
    public Producto(String nombre, Integer stock, String bodega, Double precio) {
        this.nombre = nombre;
        this.stock = stock;
        this.bodega = bodega;
        this.precio = precio;
    }

    // Getters y setters: necesarios para que JPA, Jackson (serializacion JSON)
    // y Spring puedan leer y escribir los campos del objeto.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getBodega() { return bodega; }
    public void setBodega(String bodega) { this.bodega = bodega; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
