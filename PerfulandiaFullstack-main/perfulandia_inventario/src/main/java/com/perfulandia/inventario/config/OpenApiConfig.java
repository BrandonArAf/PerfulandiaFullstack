package com.perfulandia.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para el microservicio de Inventario.
 * Esta clase configura la documentación automática de la API REST
 * utilizando OpenAPI 3.0 (anteriormente conocido como Swagger).
 * 
 * OpenAPI es una especificación estándar para documentar APIs REST,
 * permitiendo generar documentación interactiva y explorable.
 */
@Configuration // Marca esta clase como una clase de configuración de Spring
public class OpenApiConfig {

    /**
     * Configura y personaliza la documentación OpenAPI para el microservicio de Inventario.
     * Este método define la información básica de la API que aparecerá en la documentación
     * generada automáticamente por Swagger UI.
     * 
     * @return OpenAPI objeto con la configuración personalizada de la documentación
     */
    @Bean // Marca este método como un bean de Spring que será gestionado por el contenedor IoC
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Perfulandia - API de Inventario") // Título de la API que aparecerá en la documentación
                        .version("1.0.0") // Versión de la API
                        .description("API para la gestión de inventario en Perfulandia.") // Descripción general de la API
                        .contact(new Contact()
                                .name("Equipo de Perfulandia") // Nombre del equipo de desarrollo
                                .url("https://www.perfulandia.com") // URL del sitio web del proyecto
                                .email("soporte@perfulandia.com"))); // Email de contacto para soporte
    }
} 