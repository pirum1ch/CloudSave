package ru.pirum1ch.cloudsave.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String login;
    private String password;
    private String email;
}
