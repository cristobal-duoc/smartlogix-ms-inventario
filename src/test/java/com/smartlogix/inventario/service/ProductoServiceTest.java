package com.smartlogix.inventario.service;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): activa Mockito — no levanta Spring ni BD
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // @Mock: crea un doble del repositorio que no toca la base de datos
    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks: crea ProductoService con el mock inyectado
    @InjectMocks
    private ProductoService productoService;

    // Producto de prueba reutilizado en varios tests
    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        // Prepara un producto de ejemplo antes de cada test
        productoEjemplo = new Producto("Camiseta Talla M", 100, "Bodega Norte", 9990.0);
        productoEjemplo.setId(1L);
    }

    @Test
    void listarTodos_debeRetornarListaDeProductos() {
        // Arrange: cuando se llame findAll(), retornar una lista con un producto
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoEjemplo));

        // Act
        List<Producto> resultado = productoService.listarTodos();

        // Assert: la lista tiene 1 producto con el nombre correcto
        assertEquals(1, resultado.size());
        assertEquals("Camiseta Talla M", resultado.get(0).getNombre());

        // Verify: findAll fue llamado exactamente 1 vez
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarProducto() {
        // Arrange: el mock retorna el producto cuando se busca por id=1
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

        // Act
        Producto resultado = productoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Camiseta Talla M", resultado.getNombre());
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange: el repositorio no encuentra el producto
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert: el servicio lanza RuntimeException cuando el producto no existe
        assertThrows(RuntimeException.class, () -> {
            productoService.buscarPorId(99L);
        });
    }

    @Test
    void listarPorBodega_debeRetornarProductosDeLaBodega() {
        // Arrange: el mock retorna productos filtrados por bodega
        when(productoRepository.findByBodega("Bodega Norte"))
                .thenReturn(Arrays.asList(productoEjemplo));

        // Act
        List<Producto> resultado = productoService.listarPorBodega("Bodega Norte");

        // Assert: se retorna el producto de la bodega correcta
        assertEquals(1, resultado.size());
        assertEquals("Bodega Norte", resultado.get(0).getBodega());
    }

    @Test
    void guardar_debeRetornarProductoGuardado() {
        // Arrange: save() retorna el producto con id asignado
        when(productoRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

        // Act
        Producto resultado = productoService.guardar(productoEjemplo);

        // Assert: el producto retornado tiene los datos correctos
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        // Verify: save fue invocado una vez
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarStock_debeActualizarElStock() {
        // Arrange: el producto existe y save() retorna el producto modificado
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: actualizar stock a 50 unidades
        Producto resultado = productoService.actualizarStock(1L, 50);

        // Assert: el stock fue actualizado a 50
        assertEquals(50, resultado.getStock());
    }

    @Test
    void eliminar_debeInvocarDeleteById() {
        // Arrange: deleteById no hace nada (método void)
        doNothing().when(productoRepository).deleteById(1L);

        // Act
        productoService.eliminar(1L);

        // Verify: deleteById fue llamado con el id correcto
        verify(productoRepository, times(1)).deleteById(1L);
    }
}
