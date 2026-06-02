package com.esteira.notification;

import com.esteira.model.Cliente;
import com.esteira.model.Proposta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários — Decorator Pattern (Notificações).
 *
 * Verifica que cada decorator:
 *   1. Delega para o componente interno (cadeia)
 *   2. Executa seu próprio canal sem interromper os demais
 *   3. Combinações de canais chamam exatamente os decorators corretos
 */
@ExtendWith(MockitoExtension.class)
class NotificacaoDecoratorTest {

    @Mock
    private JavaMailSender mailSender;

    private Proposta proposta;

    // ----------------------------------------------------------------
    // Montagem do objeto Proposta usado em todos os testes
    // ----------------------------------------------------------------
    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente.Builder()
                .nome("João Silva")
                .cpfCnpj("123.456.789-00")
                .email("joao@email.com")
                .telefone("11999990000")
                .build();

        proposta = new Proposta.Builder()
                .codigo("PROP-20260602-00001")
                .titulo("Proposta Teste")
                .valor(5000.0)
                .cliente(cliente)
                .build();
    }

    // ================================================================
    // 1. Testes da base — NotificacaoEmail
    // ================================================================

    @Test
    @DisplayName("Email: envia mensagem quando cliente possui e-mail")
    void email_deveEnviarQuandoClienteTemEmail() {
        NotificacaoEmail email = new NotificacaoEmail(mailSender, "sistema@empresa.com");

        email.enviar(proposta, "APROVAR");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Email: não envia quando cliente não possui e-mail")
    void email_naoDeveEnviarQuandoClienteSemEmail() {
        proposta.getCliente().setEmail(null);
        NotificacaoEmail email = new NotificacaoEmail(mailSender, "sistema@empresa.com");

        email.enviar(proposta, "APROVAR");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Email: não envia quando e-mail do cliente é vazio")
    void email_naoDeveEnviarQuandoEmailVazio() {
        proposta.getCliente().setEmail("   ");
        NotificacaoEmail email = new NotificacaoEmail(mailSender, "sistema@empresa.com");

        email.enviar(proposta, "REPROVAR");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // ================================================================
    // 2. Testes do Decorator SMS
    // ================================================================

    @Test
    @DisplayName("SMS: delega para o componente base (email) e registra SMS")
    void sms_deveDelegarParaBaseERegistrarCanal() {
        // Usamos um spy na base para verificar que ela foi chamada
        NotificacaoEmail emailBase = spy(new NotificacaoEmail(mailSender, "sistema@empresa.com"));
        NotificacaoSMS sms = new NotificacaoSMS(emailBase);

        sms.enviar(proposta, "ENVIAR_ANALISE");

        // Base (e-mail) deve ter sido chamada
        verify(emailBase, times(1)).enviar(proposta, "ENVIAR_ANALISE");
        // MailSender também deve ter sido acionado
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ================================================================
    // 3. Testes do Decorator WhatsApp
    // ================================================================

    @Test
    @DisplayName("WhatsApp: delega para a cadeia interna")
    void whatsapp_deveDelegarParaCadeiaInterna() {
        NotificacaoEmail emailBase = spy(new NotificacaoEmail(mailSender, "sistema@empresa.com"));
        NotificacaoWhatsApp whatsapp = new NotificacaoWhatsApp(emailBase);

        whatsapp.enviar(proposta, "APROVAR");

        verify(emailBase, times(1)).enviar(proposta, "APROVAR");
    }

    // ================================================================
    // 4. Testes do Decorator Facebook
    // ================================================================

    @Test
    @DisplayName("Facebook: delega para a cadeia interna")
    void facebook_deveDelegarParaCadeiaInterna() {
        NotificacaoEmail emailBase = spy(new NotificacaoEmail(mailSender, "sistema@empresa.com"));
        NotificacaoFacebook facebook = new NotificacaoFacebook(emailBase);

        facebook.enviar(proposta, "REABRIR");

        verify(emailBase, times(1)).enviar(proposta, "REABRIR");
    }

    // ================================================================
    // 5. Cadeia completa — Email + SMS + WhatsApp + Facebook
    // ================================================================

    @Test
    @DisplayName("Cadeia completa: todos os decorators delegam até o email base")
    void cadeiaCompleta_deveChamarEmailUmaUnicaVez() {
        NotificacaoEmail emailBase = spy(new NotificacaoEmail(mailSender, "sistema@empresa.com"));
        Notificavel cadeia = new NotificacaoFacebook(
                                new NotificacaoWhatsApp(
                                    new NotificacaoSMS(emailBase)));

        cadeia.enviar(proposta, "APROVAR");

        // O e-mail base deve ser chamado exatamente uma vez, independente de quantos decorators existem
        verify(emailBase, times(1)).enviar(proposta, "APROVAR");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Cadeia completa: cada decorator executa sua lógica própria")
    void cadeiaCompleta_cadaDecoratorEhInstanciadoIndependente() {
        NotificacaoEmail emailBase = new NotificacaoEmail(mailSender, "sistema@empresa.com");

        NotificacaoSMS sms             = spy(new NotificacaoSMS(emailBase));
        NotificacaoWhatsApp whatsapp   = spy(new NotificacaoWhatsApp(sms));
        NotificacaoFacebook facebook   = spy(new NotificacaoFacebook(whatsapp));

        facebook.enviar(proposta, "REPROVAR");

        // Cada decorator deve ter sido chamado uma vez
        verify(facebook,  times(1)).enviar(proposta, "REPROVAR");
        verify(whatsapp,  times(1)).enviar(proposta, "REPROVAR");
        verify(sms,       times(1)).enviar(proposta, "REPROVAR");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ================================================================
    // 6. Cadeia sem canal opcional — apenas email
    // ================================================================

    @Test
    @DisplayName("Sem canais opcionais: apenas o email é enviado")
    void semCanaisOpcionais_apenasEmailEhEnviado() {
        NotificacaoEmail emailBase = new NotificacaoEmail(mailSender, "sistema@empresa.com");

        // Nenhum decorator opcional — apenas a base
        emailBase.enviar(proposta, "ENVIAR_ANALISE");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ================================================================
    // 7. Substituição de decorator em tempo de execução (runtime swap)
    // ================================================================

    @Test
    @DisplayName("Decorator pode ser composto de formas diferentes sem afetar o email base")
    void decoratorComposicaoFlexivel_naoAfetaEmailBase() {
        NotificacaoEmail emailBase = spy(new NotificacaoEmail(mailSender, "sistema@empresa.com"));

        // Composição 1: apenas SMS
        Notificavel cadeiaA = new NotificacaoSMS(emailBase);
        cadeiaA.enviar(proposta, "APROVAR");

        // Composição 2: apenas WhatsApp
        Notificavel cadeiaB = new NotificacaoWhatsApp(emailBase);
        cadeiaB.enviar(proposta, "APROVAR");

        // Email base deve ter sido chamado exatamente 2 vezes (uma por cadeia)
        verify(emailBase, times(2)).enviar(proposta, "APROVAR");
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
}
