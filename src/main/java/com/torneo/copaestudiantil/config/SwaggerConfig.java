package com.torneo.copaestudiantil.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Copa Estudiantil Cup Callao — API")
                        .description("""
                                API REST para la gestión del torneo estudiantil de fútbol.
                                
                                ## Autenticación
                                Todos los endpoints requieren token JWT excepto `/api/auth/*`.
                                1. Regístrate en `POST /api/auth/register`
                                2. Obtén el token en `POST /api/auth/login`
                                3. Haz clic en **Authorize** e ingresa: `Bearer {token}`
                                
                                ## Paginación con cursores
                                Los endpoints `/search` usan paginación por cursor.
                                Usa el `nextCursor` del response en el siguiente request.
                                
                                ## Códigos de negocio
                                Formato: `S_ENT_HTTP_SEQ` (éxito) o `E_ENT_HTTP_SEQ` (error).
                                Ejemplo: `S_JUG_201_001` = Jugador creado exitosamente.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Copa Estudiantil Cup Callao")
                                .email("admin@torneo.com"))
                        .license(new License().name("Privado")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local"),
                        new Server().url("https://api.torneo.com").description("Producción")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .name("Bearer Auth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido de POST /api/auth/login")));
    }
}
