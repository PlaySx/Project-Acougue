package br.com.acougue.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("production") // Só roda em produção
public class DatabaseCleaner {

    @Bean
    public CommandLineRunner cleanDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("!!! ATENÇÃO: INICIANDO LIMPEZA FORÇADA DO BANCO DE DADOS !!!");
            
            // Apaga as tabelas na ordem correta para evitar erros de chave estrangeira
            try {
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_order_items CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_orders CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_client_products CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_phone_numbers CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_clients CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_products CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_users CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_establishments CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_audit_log CASCADE");
                
                // Apaga tabelas antigas se existirem
                jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
                jdbcTemplate.execute("DROP TABLE IF EXISTS tb_client CASCADE");
                
                System.out.println("!!! LIMPEZA CONCLUÍDA COM SUCESSO. O HIBERNATE VAI RECRIAR TUDO. !!!");
            } catch (Exception e) {
                System.err.println("Erro ao limpar banco: " + e.getMessage());
            }
        };
    }
}
