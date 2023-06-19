package com.zer0s2m.creeptenuous.api.config;

import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.filters.JwtFilter;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    SecurityFilterChain springWebFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(
                        c -> c.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/token",
                                "/api/v1/auth/refresh",
                                "/api/v1/user/create",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    UserDetailsService customUserDetailsService(final @NotNull UserRepository users) {
        return users::findByLogin;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            @NotNull AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
