package utec.reciscore.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    @EventListener
    public void handleUsuarioRegistrado(UsuarioRegistradoEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@reciscore.com");
            message.setTo(event.getDestinatario());
            message.setSubject("¡Bienvenido a ReciScore!");
            message.setText(
                    "Hola " + event.getNombre() + ",\n\n" +
                            "¡Tu cuenta en ReciScore ha sido creada exitosamente!\n\n" +
                            "Ya puedes iniciar sesión y comenzar a registrar tus reportes de reciclaje.\n\n" +
                            "¡Gracias por contribuir con el medio ambiente!\n\n" +
                            " - El equipo de ReciScore"
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de bienvenida a {}: {}", event.getDestinatario(), e.getMessage());
        }
    }

    @Async
    @EventListener
    public void handleReciclajeValidado(ReciclajeValidadoEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@reciscore.com");
            message.setTo(event.getEmailUsuario());
            message.setSubject("¡Reciclaje registrado en ReciScore!");
            message.setText("Has ganado " + event.getPuntosGanados() +
                    " puntos por reciclar " + event.getMaterialNombre() + ". ¡Sigue así!");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de reciclaje a {}: {}", event.getEmailUsuario(), e.getMessage());
        }
    }
}