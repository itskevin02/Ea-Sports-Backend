
package com.project.msvc_game.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain filtros(
            HttpSecurity http,
            JwtFiltro jwtFiltro) throws Exception {

        http.exceptionHandling(e -> e
                .authenticationEntryPoint(
                        (req, res, ex) -> responder(
                                res, 401, "Sesión no válida o vencida"
                        )
                )
                .accessDeniedHandler(
                        (req, res, ex) -> responder(
                                res, 403, "Sin permiso para esta acción"
                        )
                )
        );

        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/error"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/games",
                                "/games/**"
                        ).permitAll()
                        .requestMatchers(
                                "/games",
                                "/games/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtFiltro,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    private static void responder(
            HttpServletResponse res,
            int estado,
            String mensaje) throws IOException {

        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(
                "{\"mensaje\": \"" + mensaje + "\"}"
        );
    }
}
