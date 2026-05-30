package utec.reciscore.email;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecuperacionContrasenaEvent extends ApplicationEvent {

    private final String destinatario;
    private final String nombre;
    private final String nuevaContrasena;

    public RecuperacionContrasenaEvent(Object source, String destinatario,
                                       String nombre, String nuevaContrasena) {
        super(source);
        this.destinatario = destinatario;
        this.nombre = nombre;
        this.nuevaContrasena = nuevaContrasena;
    }
}