package utec.reciscore.email;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UsuarioRegistradoEvent extends ApplicationEvent {

    private final String destinatario;
    private final String nombre;

    public UsuarioRegistradoEvent(Object source, String destinatario, String nombre) {
        super(source);
        this.destinatario = destinatario;
        this.nombre = nombre;
    }


}