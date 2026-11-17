
package com.project.msvc_game.config;

import com.project.msvc_game.service.JwtServicio;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtServicio jwt;

    public JwtFiltro(JwtServicio jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain cadena) throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            try {
                Claims datos = jwt.validar(header.substring(7));

                String rol = datos.get("rol", String.class);

                var autoridad = new SimpleGrantedAuthority(
                        "ROLE_" + rol
                );

                var usuario = new UsernamePasswordAuthenticationToken(
                        datos.getSubject(),
                        null,
                        List.of(autoridad)
                );

                SecurityContextHolder.getContext().setAuthentication(
                        usuario
                );

            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        cadena.doFilter(req, res);
    }
}
