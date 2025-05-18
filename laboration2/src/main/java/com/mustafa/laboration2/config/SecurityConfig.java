package com.mustafa.laboration2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

// Denna klass hanterar säkerhetsinställningar för programmet
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    // Konfigurerar säkerhetsinställningar för webbapplikationen
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Aktiverar CORS (Cross-Origin Resource Sharing) för att tillåta anrop från andra domäner
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Stänger av CSRF-skydd eftersom vi använder REST API
            .csrf(csrf -> csrf.disable())
            // Konfigurerar vilka URL:er som är tillåtna och vilka som kräver inloggning
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()  // Inloggningssidan är tillgänglig för alla
                .requestMatchers("/", "/index.html").permitAll()  // Startsidan är tillgänglig för alla
                .requestMatchers(HttpMethod.GET, "/api/kategorier/**").permitAll()  // Kategorier kan läsas av alla
                .requestMatchers(HttpMethod.GET, "/api/platser", "/api/platser/{id}").permitAll()  // Platser kan läsas av alla
                .requestMatchers(HttpMethod.POST, "/api/kategorier").hasRole("ADMIN")  // Endast admin kan lägga till kategorier
                .anyRequest().authenticated()  // Alla andra sidor kräver inloggning
            )
            // Aktiverar grundläggande HTTP-autentisering
            .httpBasic(basic -> {})
            // Konfigurerar inloggningsformuläret
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/api/platser", true)
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid credentials\"}");
                })
                .permitAll()
            )
            // Hanterar fel när någon försöker komma åt skyddade sidor
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}");
                })
            );

        return http.build();
    }

    // Konfigurerar CORS-inställningar för att tillåta anrop från andra domäner
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));  // Tillåter anrop från alla domäner
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Tillåtna HTTP-metoder
        configuration.setAllowedHeaders(Arrays.asList("*"));  // Tillåter alla headers
        configuration.setExposedHeaders(Arrays.asList("Authorization"));  // Visar Authorization-header
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Skapar en användare i minnet för testning
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build()
        );
    }

    // Skapar en krypterare för lösenord
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
