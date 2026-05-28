package com.esteira.config;
import com.esteira.notification.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
@Configuration
public class NotificacaoConfig {
    @Value("${spring.mail.from}") private String emailFrom;

    /**
     * Expõe apenas o NotificacaoEmail como bean base.
     * A cadeia completa (com decorators opcionais) é montada
     * dinamicamente em EsteiraService conforme os canais selecionados.
     */
    @Bean
    public NotificacaoEmail notificacaoEmail(JavaMailSender mailSender) {
        return new NotificacaoEmail(mailSender, emailFrom);
    }
}
