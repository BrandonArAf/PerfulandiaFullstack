package com.perfulandia.perfulandia_producto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Perfulandia - API de Productos")
                        .version("1.0.0")
                        .description("API para la gestión de productos en Perfulandia.")
                        .contact(new Contact()
                                .name("Equipo de Perfulandia")
                                .url("https://www.perfulandia.com")
                                .email("soporte@perfulandia.com")));
    }
} 