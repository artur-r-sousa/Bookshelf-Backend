package com.bookshelf.bookshelf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers("/api/auth/register").permitAll()
            .requestMatchers("/products", "/public/**").permitAll()
            .requestMatchers("/api/books/bestsellers").permitAll()
            .requestMatchers("/api/books/search").permitAll()
            .requestMatchers("/api/categories/").permitAll()
            .requestMatchers("/api/categories/books").permitAll()
            .requestMatchers("/api/auth/login/google").permitAll()
            .requestMatchers("/api/auth/google").permitAll()
            .requestMatchers("/api/confirm").permitAll()
            .requestMatchers("/api/auth/recover-password").permitAll()
            .requestMatchers("/api/auth/update-password").permitAll()
            .requestMatchers("/admin/**", "/user/**").authenticated()
            .anyRequest().authenticated())
        .cors(customizer -> customizer.configurationSource(corsConfigurationSource()));

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManager.class);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("*"); 
    configuration.addAllowedMethod("*"); 
    configuration.addAllowedHeader("*"); 
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
