package ru.pirum1ch.cloudsave.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonAutoDetect
public class FileDto {

    private Long id;

    private String filename;

    private Long size;

    private String key;

    private String extention;

    private Date uploadDate;

}
