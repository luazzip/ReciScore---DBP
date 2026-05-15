package utec.reciscore.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String username;

    @Min(value = 0, message = "Los puntos no pueden ser negativos")
    @Column(nullable = false)
    private Integer points = 0;

    @DecimalMin(value = "1.0", message = "El multiplicador debe ser mayor o igual a 1")
    @Column(nullable = false)
    private Double multiplier = 1.0;

    private URL profilePicture; //antes string

    private String location;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer reciclajes = 0;
}
