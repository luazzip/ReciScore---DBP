package utec.reciscore.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    @EventListener
    public void handleUsuarioRegistrado(UsuarioRegistradoEvent event) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombre", event.getNombre());
            String html = templateEngine.process("email/bienvenido", ctx);
            enviarEmailHtml(event.getDestinatario(), "¡Bienvenido a ReciScore! 🌱", html);
            log.info("Email de bienvenida enviado a {}", event.getDestinatario());
        } catch (MessagingException | MailException e) {
            log.error("Error al enviar email de bienvenida a {}: {}", event.getDestinatario(), e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleReciclajeValidado(ReciclajeValidadoEvent event) {
        try {
            Context ctx = new Context();
            ctx.setVariable("puntosGanados", event.getPuntosGanados());
            ctx.setVariable("materialNombre", event.getMaterialNombre());
            String html = templateEngine.process("email/reciclaje-validado", ctx);
            enviarEmailHtml(event.getEmailUsuario(), "¡Reciclaje registrado en ReciScore! ♻️", html);
            log.info("Email de reciclaje validado enviado a {}", event.getEmailUsuario());
        } catch (MessagingException | MailException e) {
            log.error("Error al enviar email de reciclaje validado a {}: {}", event.getEmailUsuario(), e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleRecuperacionContrasena(RecuperacionContrasenaEvent event) {
        try {
            Context ctx = new Context();
            ctx.setVariable("nombre", event.getNombre());
            ctx.setVariable("nuevaContrasena", event.getNuevaContrasena());
            String html = templateEngine.process("email/recuperacion-contrasena", ctx);
            enviarEmailHtml(event.getDestinatario(), "Recuperación de contraseña – ReciScore 🔒", html);
            log.info("Email de recuperación enviado a {}", event.getDestinatario());
        } catch (MessagingException | MailException e) {
            log.error("Error al enviar email de recuperación a {}: {}", event.getDestinatario(), e.getMessage());
        }
    }

    private void enviarEmailHtml(String destinatario, String asunto, String htmlContenido)
            throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(htmlContenido, true);
        mailSender.send(mensaje);
    }
}