package br.com.acougue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.acougue.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    private Environment environment;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .disable()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Versão atualizada
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> {
                // H2 Console (apenas para desenvolvimento)
                if (isDevelopment()) {
                    auth.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll();
                }
                
                auth
                    // Endpoints públicos
                    .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/users/register")).permitAll()
                    
                    // Endpoints protegidos - requer autenticação
                    .requestMatchers(new AntPathRequestMatcher("/users/**")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/estabelecimento/**")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/clients/**")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/products/**")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/orders/**")).authenticated()
                    .requestMatchers(new AntPathRequestMatcher("/dashboard/**")).authenticated()
                    
                    // Qualquer outra requisição requer autenticação
                    .anyRequest().authenticated();
            });
            // TODO: Adicionar JWT filter quando implementar JWT

        return http.build();
    }
    
    /**
     * Verifica se está em ambiente de desenvolvimento
     */
    private boolean isDevelopment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length == 0 || // Profile padrão
               java.util.Arrays.asList(profiles).contains("dev") ||
               java.util.Arrays.asList(profiles).contains("development") ||
               java.util.Arrays.asList(profiles).contains("local");
    }
}