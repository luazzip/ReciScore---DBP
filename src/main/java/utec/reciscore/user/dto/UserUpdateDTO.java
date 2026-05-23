package utec.reciscore.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String name;
    private String profilePicture;
    private String location;
}