package ru.pirum1ch.cloudsave.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "login")
    private String login; ;

    @Column(name = "token", columnDefinition = "varchar(400)")
    private String token;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;

    @Column(name = "create_date")
    private Date date;

    @Override
    public String toString() {
        return "Token {" +
                "id=" + id + ", \n" +
                " login='" + login + '\'' + ", \n" +
                " token='" + token + '\'' + ", \n" +
                " date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token1)) return false;
        return Objects.equals(login, token1.login) && Objects.equals(token, token1.token) && Objects.equals(date, token1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, token, date);
    }
}
