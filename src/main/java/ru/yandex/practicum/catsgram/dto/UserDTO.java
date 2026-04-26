package ru.yandex.practicum.catsgram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant registrationDate = Instant.now();  //in example INSTANT
}
