package utec.reciscore.email;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReciclajeValidadoEvent extends ApplicationEvent {
    private final String emailUsuario;
    private final int puntosGanados;
    private final String materialNombre;

    public ReciclajeValidadoEvent(Object source, String emailUsuario,
                                  int puntosGanados, String materialNombre) {
        super(source);
        this.emailUsuario = emailUsuario;
        this.puntosGanados = puntosGanados;
        this.materialNombre = materialNombre;
    }
}