package utec.reciscore.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

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

    private String profilePicture;
    private String location;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer reciclajes = 0;
}
