package com.perfulandia.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI para el microservicio de Usuario
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
                        .title("API de Usuarios - Perfulandia")
                        .description("""
                                Microservicio de gestión de usuarios para la plataforma Perfulandia.
                                
                                ## Funcionalidades principales:
                                - Registro y gestión de usuarios
                                - Actualización y eliminación de cuentas
                                - Búsqueda de usuarios por ID
                                
                                ## Endpoints disponibles:
                                - `GET /api/v1/usuario` - Listar todos los usuarios
                                - `POST /api/v1/usuario` - Crear un nuevo usuario
                                - `GET /api/v1/usuario/{id}` - Obtener usuario por ID
                                - `PUT /api/v1/usuario/{id}` - Actualizar usuario
                                - `PATCH /api/v1/usuario/{id}` - Actualización parcial
                                - `DELETE /api/v1/usuario/{id}` - Eliminar usuario
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
                                .url("https://api.perfulandia.com/usuarios")
                                .description("Servidor de producción")
                ));
    }
} 