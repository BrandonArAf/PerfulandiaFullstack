package com.perfulandia.inventario.service;

import com.perfulandia.inventario.model.Inventario;
import com.perfulandia.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la gestión de inventario.
 * Esta clase contiene la lógica de negocio para las operaciones de inventario,
 * actuando como intermediario entre el controlador y el repositorio.
 * 
 * El servicio implementa:
 * - Lógica de negocio para operaciones CRUD
 * - Manejo de excepciones con mensajes descriptivos
 * - Validaciones de datos antes de operaciones críticas
 * - Operaciones específicas de inventario (ajustar cantidades)
 */
@Service // Marca esta clase como un servicio de Spring, permitiendo la inyección de dependencias
public class InventarioService {
    
    private final InventarioRepository repository; // Repositorio para acceder a la base de datos

    /**
     * Constructor que recibe el repositorio por inyección de dependencias.
     * Spring automáticamente inyecta una instancia del repositorio cuando crea este servicio.
     * 
     * @param repository Repositorio para operaciones de base de datos
     */
    public InventarioService(InventarioRepository repository) {
        this.repository = repository; // Asigna el repositorio inyectado
    }

    /**
     * Obtiene todos los registros de inventario de la base de datos.
     * Este método retorna una lista completa de todos los registros de inventario
     * sin ningún filtro o procesamiento adicional.
     * 
     * @return Lista de todos los registros de inventario
     */
    public List<Inventario> findAll() {
        return repository.findAll(); // Delega la operación al repositorio
    }

    /**
     * Busca un registro de inventario por el ID del producto.
     * Este método es útil cuando se necesita encontrar el inventario
     * de un producto específico usando su ID.
     * 
     * @param productoId ID del producto para buscar en inventario
     * @return Registro de inventario encontrado
     * @throws RuntimeException si no se encuentra el inventario para el producto especificado
     */
    public Inventario findByProductoId(Long productoId) {
        // Busca el inventario en la base de datos
        Inventario inventario = repository.findByProductoId(productoId);
        
        // Si no se encuentra, lanza una excepción con mensaje descriptivo
        if (inventario == null) {
            throw new RuntimeException("Inventario no encontrado para el producto con id: " + productoId);
        }
        
        return inventario; // Retorna el inventario encontrado
    }

    /**
     * Busca un registro de inventario por su ID único.
     * Este método es útil para operaciones que requieren el ID específico
     * del registro de inventario (no del producto).
     * 
     * @param id ID único del registro de inventario
     * @return Registro de inventario encontrado
     * @throws RuntimeException si no se encuentra el inventario con el ID especificado
     */
    public Inventario findById(Long id) {
        // Busca el inventario por ID y lanza excepción si no existe
        return repository.findById(id).orElseThrow(() -> 
            new RuntimeException("Inventario no encontrado con id: " + id));
    }

    /**
     * Guarda un registro de inventario en la base de datos.
     * Este método puede crear un nuevo registro o actualizar uno existente,
     * dependiendo de si el inventario ya tiene un ID asignado.
     * 
     * @param inventario Registro de inventario a guardar
     * @return Registro de inventario guardado (con ID asignado si es nuevo)
     */
    public Inventario save(Inventario inventario) {
        return repository.save(inventario); // Delega la operación al repositorio
    }

    /**
     * Ajusta la cantidad disponible de un producto en inventario.
     * Este método permite aumentar o reducir el stock de un producto
     * sumando la cantidad especificada a la cantidad actual.
     * 
     * @param productoId ID del producto cuyo stock se va a ajustar
     * @param cantidad Cantidad a sumar (positiva para aumentar, negativa para reducir)
     * @return Registro de inventario actualizado
     * @throws RuntimeException si no se encuentra el inventario para el producto especificado
     */
    public Inventario ajustarCantidad(Long productoId, int cantidad) {
        // Busca el inventario existente
        Inventario inventario = repository.findByProductoId(productoId);
        
        // Si no se encuentra, lanza una excepción con mensaje descriptivo
        if (inventario == null) {
            throw new RuntimeException("Inventario no encontrado para el producto con id: " + productoId);
        }
        
        // Ajusta la cantidad sumando la cantidad especificada
        inventario.setCantidadDisponible(inventario.getCantidadDisponible() + cantidad);
        
        // Guarda los cambios en la base de datos
        return repository.save(inventario);
    }

    /**
     * Elimina un registro de inventario de la base de datos.
     * Este método verifica que el inventario existe antes de eliminarlo
     * para proporcionar un mensaje de error más descriptivo.
     * 
     * @param id ID del registro de inventario a eliminar
     * @throws RuntimeException si no se encuentra el inventario con el ID especificado
     */
    public void deleteById(Long id) {
        // Verifica que el inventario existe antes de eliminarlo
        if (!repository.existsById(id)) {
            throw new RuntimeException("Inventario no encontrado con id: " + id);
        }
        
        // Elimina el inventario de la base de datos
        repository.deleteById(id);
    }
}