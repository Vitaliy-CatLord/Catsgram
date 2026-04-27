package ru.yandex.practicum.catsgram.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Post {
    private long id;
    private User author;
    private String description;
    private Instant postDate;
    private List<Image> images;
}
