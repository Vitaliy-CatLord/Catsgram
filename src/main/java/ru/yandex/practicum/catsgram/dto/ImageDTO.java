package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class ImageDTO {
    private long id;
    private long postId;
    private String fileName;
    private byte[] data;
}
