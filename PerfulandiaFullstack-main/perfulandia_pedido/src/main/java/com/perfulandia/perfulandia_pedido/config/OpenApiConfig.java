package com.perfulandia.perfulandia_pedido.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI para el microservicio de Pedidos
 * 
 * Esta clase configura la documentación de la API REST usando OpenAPI 3.0,
 * proporcionando información detallada sobre los endpoints, modelos de datos
 * y operaciones disponibles en el microservicio.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Pedidos - Perfulandia")
                        .description("""
                                Microservicio de gestión de pedidos para la plataforma Perfulandia.
                                
                                ## Funcionalidades principales:
                                - Creación y gestión de pedidos
                                - Validación de stock de productos
                                - Verificación de existencia de usuarios
                                - Integración con microservicios de pago e inventario
                                
                                ## Endpoints disponibles:
                                - `GET /api/v1/pedido` - Listar todos los pedidos
                                - `POST /api/v1/pedido` - Crear un nuevo pedido
                                - `GET /api/v1/pedido/{id}` - Obtener pedido por ID
                                - `PUT /api/v1/pedido/{id}` - Actualizar pedido
                                - `DELETE /api/v1/pedido/{id}` - Eliminar pedido
                                - `PATCH /api/v1/pedido/{id}` - Actualización parcial
                                - `GET /api/v1/pedido/verificar-stock/{productoId}/{cantidad}` - Verificar stock
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Perfulandia")
                                .email("desarrollo@perfulandia.com")
                                .url("https://perfulandia.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.perfulandia.com/pedidos")
                                .description("Servidor de producción")
                ));
    }
} 