package com.perfulandia.pago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI para el microservicio de Pago
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
                        .title("API de Pagos - Perfulandia")
                        .description("""
                                Microservicio de gestión de pagos para la plataforma Perfulandia.
                                
                                ## Funcionalidades principales:
                                - Procesamiento y gestión de pagos
                                - Validación de métodos de pago
                                - Seguimiento del estado de transacciones
                                - Integración con microservicios de pedidos
                                
                                ## Endpoints disponibles:
                                - `GET /api/pagos` - Listar todos los pagos
                                - `POST /api/pagos` - Crear un nuevo pago
                                - `GET /api/pagos/{id}` - Obtener pago por ID
                                - `PUT /api/pagos/{id}` - Actualizar pago
                                - `DELETE /api/pagos/{id}` - Eliminar pago
                                - `GET /api/pagos/pedido/{pedidoId}` - Obtener pagos por pedido
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
                                .url("http://localhost:8084")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.perfulandia.com/pagos")
                                .description("Servidor de producción")
                ));
    }
} 