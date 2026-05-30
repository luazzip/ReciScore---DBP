package utec.reciscore.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private TemplateEngine templateEngine;
    @Mock private MimeMessage mimeMessage;

    @InjectMocks private EmailService emailService;

    private final String EMAIL_USUARIO   = "juan@example.com";
    private final String NOMBRE_USUARIO  = "Juan";
    private final String MATERIAL_NOMBRE = "Plástico";
    private final int    PUNTOS_GANADOS  = 50;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // ─── handleUsuarioRegistrado ───────────────────────────────────────────────

    @Test
    void handleUsuarioRegistrado_debeProcesarPlantillaCorrectaYEnviarEmail() {
        when(templateEngine.process(eq("email/bienvenido"), any(Context.class)))
                .thenReturn("<html>Bienvenido Juan</html>");

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);
        emailService.handleUsuarioRegistrado(event);

        verify(templateEngine).process(eq("email/bienvenido"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void handleUsuarioRegistrado_debeLlamarMailSenderExactamenteUnaVez() {
        when(templateEngine.process(eq("email/bienvenido"), any(Context.class)))
                .thenReturn("<html>Bienvenido</html>");

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);
        emailService.handleUsuarioRegistrado(event);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void handleUsuarioRegistrado_noDebeLanzarExcepcionSiSMTPFalla() {
        when(templateEngine.process(eq("email/bienvenido"), any(Context.class)))
                .thenReturn("<html>Bienvenido</html>");
        doThrow(new MailSendException("SMTP caído"))
                .when(mailSender).send(any(MimeMessage.class));

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        // el catch interno no debe dejar explotar la app
        emailService.handleUsuarioRegistrado(event);

        verify(mailSender).send(mimeMessage);
    }

    // ─── handleReciclajeValidado ───────────────────────────────────────────────

    @Test
    void handleReciclajeValidado_debeProcesarPlantillaCorrectaYEnviarEmail() {
        when(templateEngine.process(eq("email/reciclaje-validado"), any(Context.class)))
                .thenReturn("<html>+50 puntos</html>");

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);
        emailService.handleReciclajeValidado(event);

        verify(templateEngine).process(eq("email/reciclaje-validado"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void handleReciclajeValidado_debeLlamarMailSenderExactamenteUnaVez() {
        when(templateEngine.process(eq("email/reciclaje-validado"), any(Context.class)))
                .thenReturn("<html>+50 puntos</html>");

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);
        emailService.handleReciclajeValidado(event);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void handleReciclajeValidado_noDebeLanzarExcepcionSiSMTPFalla() {
        when(templateEngine.process(eq("email/reciclaje-validado"), any(Context.class)))
                .thenReturn("<html>+50 puntos</html>");
        doThrow(new MailSendException("SMTP caído"))
                .when(mailSender).send(any(MimeMessage.class));

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void handleReciclajeValidado_conCeroPuntosDebeEnviarEmailIgual() {
        when(templateEngine.process(eq("email/reciclaje-validado"), any(Context.class)))
                .thenReturn("<html>+0 puntos</html>");

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, 0, MATERIAL_NOMBRE);
        emailService.handleReciclajeValidado(event);

        verify(mailSender).send(mimeMessage);
    }

    // ─── handleRecuperacionContrasena ─────────────────────────────────────────

    @Test
    void handleRecuperacionContrasena_debeProcesarPlantillaCorrectaYEnviarEmail() {
        when(templateEngine.process(eq("email/recuperacion-contrasena"), any(Context.class)))
                .thenReturn("<html>Nueva contraseña: abc123</html>");

        RecuperacionContrasenaEvent event =
                new RecuperacionContrasenaEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO, "abc123");
        emailService.handleRecuperacionContrasena(event);

        verify(templateEngine).process(eq("email/recuperacion-contrasena"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void handleRecuperacionContrasena_noDebeLanzarExcepcionSiSMTPFalla() {
        when(templateEngine.process(eq("email/recuperacion-contrasena"), any(Context.class)))
                .thenReturn("<html>Nueva contraseña</html>");
        doThrow(new MailSendException("SMTP caído"))
                .when(mailSender).send(any(MimeMessage.class));

        RecuperacionContrasenaEvent event =
                new RecuperacionContrasenaEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO, "abc123");

        emailService.handleRecuperacionContrasena(event);

        verify(mailSender).send(mimeMessage);
    }
}