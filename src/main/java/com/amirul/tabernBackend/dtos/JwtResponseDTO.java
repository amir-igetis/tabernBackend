package com.amirul.tabernBackend.dtos;

import com.amirul.tabernBackend.model.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;

    public JwtResponseDTO(String token, Long id, String username, String email,
                          String firstName, String lastName, UserRole role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
