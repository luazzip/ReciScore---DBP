package utec.reciscore.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String username;
    private Integer points;
    private Double multiplier;
    private String profilePicture;
    private String location;
    private Integer reciclajes;
}
