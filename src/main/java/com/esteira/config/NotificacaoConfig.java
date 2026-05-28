package com.esteira.config;
import com.esteira.notification.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
@Configuration
public class NotificacaoConfig {
    @Value("${spring.mail.from}") private String emailFrom;
    @Bean
    public Notificavel notificacaoChain(JavaMailSender mailSender) {
        return new NotificacaoFacebook(new NotificacaoWhatsApp(new NotificacaoSMS(new NotificacaoEmail(mailSender, emailFrom))));
    }
}
