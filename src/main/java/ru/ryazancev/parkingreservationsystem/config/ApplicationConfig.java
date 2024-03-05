package ru.ryazancev.parkingreservationsystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.web.security.jwt.JwtTokenFilter;
import ru.ryazancev.parkingreservationsystem.web.security.jwt.JwtTokenProvider;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components()
                                .addSecuritySchemes("bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                )
                .info(new Info()
                        .title("Parking Reservation System REST API")
                        .description("Spring Boot Application")
                        .version("1.0"));
    }

    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity httpSecurity)
            throws Exception {
        httpSecurity
                .cors(cors ->
                        cors.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(
                                    List.of("http://localhost:3000",
                                            "https://parkmeclient-production.up.railway.app/"));
                            config.setAllowedMethods(
                                    List.of("GET",
                                            "POST",
                                            "PUT",
                                            "DELETE",
                                            "OPTIONS"));
                            config.setAllowedHeaders(
                                    List.of("Authorization",
                                            "Content-Type",
                                            "Accept"));
                            config.setAllowCredentials(true);
                            config.setMaxAge(3600L);
                            return config;
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED.value());
                                    response.getWriter()
                                            .write("Unauthorized");
                                })
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(
                                            HttpStatus.FORBIDDEN.value());
                                    response.getWriter()
                                            .write("Access denied");
                                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAuthority(
                                Role.ROLE_ADMIN.name())
                        .requestMatchers("/graphiql").permitAll()
                        .anyRequest().authenticated())
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
