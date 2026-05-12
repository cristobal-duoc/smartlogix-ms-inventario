# ms-inventario

Microservicio de gestión de inventario para SmartLogix.

## Responsabilidad

Gestiona el stock de productos por bodega. Permite a las PYMEs consultar, actualizar y eliminar productos en su inventario en tiempo real.

## Puerto

`8081`

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA (Hibernate)
- PostgreSQL

## Patrones de diseño

- **Repository Pattern**: `ProductoRepository extends JpaRepository` abstrae las operaciones de base de datos. Spring Data JPA genera la implementación automáticamente.

## Arquitectura en capas

```
Controller → Service → Repository → Entity → Base de datos
```

| Clase | Responsabilidad |
|-------|----------------|
| `ProductoController` | Recibe peticiones HTTP, responde JSON |
| `ProductoService` | Contiene la lógica de negocio |
| `ProductoRepository` | Acceso a datos (Repository Pattern) |
| `Producto` | Entidad JPA mapeada a tabla `productos` |

## Base de datos

```sql
CREATE DATABASE inventario_db;
```

## Configuración

1. Copiar `application-example.properties` como `application-local.properties`
2. Completar la contraseña de PostgreSQL
3. Exportar la variable de entorno: `export DB_PASSWORD=tu_password`

## Ejecutar

```bash
mvn spring-boot:run
```

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/inventario/productos` | Lista todos los productos |
| GET | `/inventario/productos/{id}` | Busca producto por ID |
| GET | `/inventario/productos/bodega/{bodega}` | Filtra por bodega |
| POST | `/inventario/productos` | Crea un producto |
| PUT | `/inventario/productos/{id}/stock` | Actualiza el stock |
| DELETE | `/inventario/productos/{id}` | Elimina un producto |

## Tests

```bash
mvn test
```

Pruebas unitarias en `src/test/java/com/smartlogix/inventario/service/ProductoServiceTest.java`
