package ru.pirum1ch.cloudsave.models;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "uploads")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "key")
    private String key;

    @Column(name = "extention")
    private String extention;

    @Column(name = "uploadDate")
    private Date uploadDate;


    @Override
    public String toString() {
        return "File name= '" + name + '\'' +
                ", size=" + size +
                ", extention='" + extention + '\'' +
                ", dateOfCreation=" + uploadDate +
                ';';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(name, file.name)
                && Objects.equals(size, file.size)
                && Objects.equals(key, file.key)
                && Objects.equals(extention, file.extention)
                && Objects.equals(uploadDate, file.uploadDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, key, extention, uploadDate);
    }
}
