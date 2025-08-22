package br.com.acougue.config;

import java.util.Arrays;

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
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir origens específicas - adicione seu domínio do Railway aqui
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://project-acoogue-production.up.railway.app",
            "https://*.railway.app",
            "http://localhost:*",
            "https://localhost:*"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        
        // DEBUG - Remove depois que resolver
        System.out.println("=== SECURITY CONFIG DEBUG ===");
        System.out.println("Profiles ativos: " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("É desenvolvimento: " + isDevelopment());
        System.out.println("=============================");
        
        // Crie o MvcRequestMatcher.Builder aqui
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        // registra o authentication provider
        http.authenticationProvider(authenticationProvider());

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .and()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // H2 console apenas em dev
                if (isDevelopment()) {
                    auth.requestMatchers(mvcMatcherBuilder.pattern("/h2-console/**")).permitAll();
                }

                                    // Endpoints públicos - IMPORTANTE: ordem importa!
                auth
                    // Health check e endpoints técnicos
                    .requestMatchers(mvcMatcherBuilder.pattern("/actuator/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/health")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/v3/api-docs/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui.html")).permitAll()
                    
                    // Endpoint raiz - MUITO IMPORTANTE para Railway
                    .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/index.html")).permitAll()
                    
                    // Endpoints de autenticação - ORDEM ESPECÍFICA IMPORTANTE
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/status")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/register")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/login")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/**")).permitAll()
                    
                    // Registro de usuários também público
                    .requestMatchers(mvcMatcherBuilder.pattern("/users/register")).permitAll()
                    
                    // Recursos estáticos se houver
                    .requestMatchers(mvcMatcherBuilder.pattern("/css/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/js/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/images/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/error")).permitAll()
                    
                    // Endpoints protegidos
                    .requestMatchers(mvcMatcherBuilder.pattern("/users/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/estabelecimento/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/clients/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/products/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/orders/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/dashboard/**")).authenticated()
                    
                    // Qualquer outra requisição requer autenticação
                    .anyRequest().authenticated();
            });

        return http.build();
    }
    
    private boolean isDevelopment() {
        String[] profiles = environment.getActiveProfiles();
        
        // Se não há profiles ativos, verifica se está em ambiente local
        if (profiles.length == 0) {
            // Verifica se está rodando localmente
            String port = environment.getProperty("server.port", "8080");
            return "8080".equals(port) || "3000".equals(port);
        }
        
        return Arrays.asList(profiles).contains("dev") ||
               Arrays.asList(profiles).contains("development") ||
               Arrays.asList(profiles).contains("local");
    }
}