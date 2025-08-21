package br.com.acougue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
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
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        
        // Crie o MvcRequestMatcher.Builder aqui
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        // registra o authentication provider
        http.authenticationProvider(authenticationProvider());

        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // H2 console em dev. Use o mvcMatcherBuilder
                if (isDevelopment()) {
                    auth.requestMatchers(mvcMatcherBuilder.pattern("/h2-console/**")).permitAll();
                }

                // Endpoints públicos (colocar os mais específicos antes dos genéricos)
                auth
                    // Use mvcMatcherBuilder para os endpoints Spring MVC
                    .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/actuator/health")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/v3/api-docs/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/users/register")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/auth/**")).permitAll()
                    
                    // demais endpoints protegidos
                    .requestMatchers(mvcMatcherBuilder.pattern("/users/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/estabelecimento/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/clients/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/products/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/orders/**")).authenticated()
                    .requestMatchers(mvcMatcherBuilder.pattern("/dashboard/**")).authenticated()
                    
                    .anyRequest().authenticated();
            });

        // TODO: se adicionar filtro JWT, adicione .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        return http.build();
    }
    
    private boolean isDevelopment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length == 0 ||
               java.util.Arrays.asList(profiles).contains("dev") ||
               java.util.Arrays.asList(profiles).contains("development") ||
               java.util.Arrays.asList(profiles).contains("local");
    }
}