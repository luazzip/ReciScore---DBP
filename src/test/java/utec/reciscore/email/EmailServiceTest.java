package utec.reciscore.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String EMAIL_USUARIO   = "juan@example.com";
    private final String NOMBRE_USUARIO  = "Juan";
    private final String MATERIAL_NOMBRE = "Plástico";
    private final int    PUNTOS_GANADOS  = 50;


    @Test
    void handleUsuarioRegistrado_debeEnviarCorreoConAsuntoBienvenida() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        emailService.handleUsuarioRegistrado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        assertThat(mensaje.getSubject()).isEqualTo("¡Bienvenido a ReciScore!");
    }

    @Test
    void handleUsuarioRegistrado_debeEnviarCorreoAlDestinatarioCorrecto() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        emailService.handleUsuarioRegistrado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getTo()).containsExactly(EMAIL_USUARIO);
    }

    @Test
    void handleUsuarioRegistrado_cuerpoDebeContenerNombreDelUsuario() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        emailService.handleUsuarioRegistrado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String cuerpo = captor.getValue().getText();
        assertThat(cuerpo).contains(NOMBRE_USUARIO);
    }

    @Test
    void handleUsuarioRegistrado_cuerpoDebeContenerMensajeDeBienvenida() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        emailService.handleUsuarioRegistrado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String cuerpo = captor.getValue().getText();
        assertThat(cuerpo).contains("ReciScore");
    }

    @Test
    void handleUsuarioRegistrado_debeLlamarMailSenderExactamenteUnaVez() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, EMAIL_USUARIO, NOMBRE_USUARIO);

        emailService.handleUsuarioRegistrado(event);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }



    @Test
    void handleReciclajeValidado_debeEnviarCorreoConAsuntoCorrecto() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getSubject())
                .isEqualTo("¡Reciclaje registrado en ReciScore!");
    }

    @Test
    void handleReciclajeValidado_debeEnviarCorreoAlEmailDelUsuario() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getTo()).containsExactly(EMAIL_USUARIO);
    }

    @Test
    void handleReciclajeValidado_cuerpoDebeContenerPuntosGanados() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText())
                .contains(String.valueOf(PUNTOS_GANADOS));
    }

    @Test
    void handleReciclajeValidado_cuerpoDebeContenerNombreDelMaterial() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains(MATERIAL_NOMBRE);
    }

    @Test
    void handleReciclajeValidado_debeLlamarMailSenderExactamenteUnaVez() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, PUNTOS_GANADOS, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void handleReciclajeValidado_conCeroPuntosCuerpoDebeIndicarCero() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, EMAIL_USUARIO, 0, MATERIAL_NOMBRE);

        emailService.handleReciclajeValidado(event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains("0");
    }
}