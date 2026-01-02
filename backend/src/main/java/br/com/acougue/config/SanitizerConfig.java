package br.com.acougue.config;

import org.owasp.html.HtmlPolicyBuilder; // Importa o Builder
import org.owasp.html.PolicyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SanitizerConfig {

    /**
     * Cria uma política de sanitização que remove todas as tags HTML,
     * prevenindo ataques de XSS. Permite apenas texto puro.
     * @return Uma PolicyFactory configurada.
     */
    @Bean
    public PolicyFactory htmlPolicy() {
        // CORREÇÃO: Usando o HtmlPolicyBuilder para criar uma política vazia (sem tags permitidas)
        return new HtmlPolicyBuilder().toFactory();
    }
}
