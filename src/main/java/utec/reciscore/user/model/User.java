package utec.reciscore.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
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

    @Column(nullable = false)
    private Integer nivel = 1;

    public static int calcularNivel(int points) {
        return Math.floorDiv(points, 2500) + 1;
    }

    @Column(nullable = false)
    private Integer rachaDias = 0;

    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoLogin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<utec.reciscore.desafio.model.UsuarioDesafio> desafiosInscritos = new HashSet<>();

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ── UserDetails ──
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getUsername() { return email; }  // Spring usa email como username
    public String getDisplayUsername() { return this.username; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}