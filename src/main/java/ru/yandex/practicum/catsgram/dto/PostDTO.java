package ru.yandex.practicum.catsgram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class PostDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private UserDTO author;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant postDate;
    private List<Long> images;
}
