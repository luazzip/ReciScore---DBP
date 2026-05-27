package utec.reciscore.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailEventsTest {

    @Test
    void usuarioRegistradoEvent_debeGuardarDestinatarioCorrectamente() {
        String destinatario = "maria@example.com";
        String nombre       = "María";

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, destinatario, nombre);

        assertThat(event.getDestinatario()).isEqualTo(destinatario);
    }

    @Test
    void usuarioRegistradoEvent_debeGuardarNombreCorrectamente() {
        String destinatario = "maria@example.com";
        String nombre       = "María";

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, destinatario, nombre);

        assertThat(event.getNombre()).isEqualTo(nombre);
    }

    @Test
    void usuarioRegistradoEvent_sourcDebeSerElObjetoEmisor() {
        Object source = new Object();

        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(source, "a@b.com", "Ana");

        assertThat(event.getSource()).isSameAs(source);
    }

    @Test
    void usuarioRegistradoEvent_conNombreVacioDebeGuardarCadenaVacia() {
        UsuarioRegistradoEvent event =
                new UsuarioRegistradoEvent(this, "x@x.com", "");

        assertThat(event.getNombre()).isEmpty();
    }


    @Test
    void reciclajeValidadoEvent_debeGuardarEmailCorrectamente() {
        String email = "pedro@example.com";

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, email, 30, "Vidrio");

        assertThat(event.getEmailUsuario()).isEqualTo(email);
    }

    @Test
    void reciclajeValidadoEvent_debeGuardarPuntosGanados() {
        int puntos = 75;

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, "p@p.com", puntos, "Papel");

        assertThat(event.getPuntosGanados()).isEqualTo(puntos);
    }

    @Test
    void reciclajeValidadoEvent_debeGuardarNombreMaterial() {
        String material = "Aluminio";

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, "p@p.com", 10, material);

        assertThat(event.getMaterialNombre()).isEqualTo(material);
    }

    @Test
    void reciclajeValidadoEvent_sourceDebeSerElObjetoEmisor() {
        Object source = new Object();

        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(source, "r@r.com", 20, "Plástico");

        assertThat(event.getSource()).isSameAs(source);
    }

    @Test
    void reciclajeValidadoEvent_conPuntosNegativosDebeGuardarValorNegativo() {
        ReciclajeValidadoEvent event =
                new ReciclajeValidadoEvent(this, "r@r.com", -5, "Metal");

        assertThat(event.getPuntosGanados()).isNegative();
    }
}