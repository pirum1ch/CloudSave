package ru.pirum1ch.cloudsave.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TokenDTO {

    private Long id;
    private String login; ;
    private String token;
    private boolean isActive;
    private Date date;
}
