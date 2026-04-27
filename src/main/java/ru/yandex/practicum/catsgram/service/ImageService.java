package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.ImageDTO;
import ru.yandex.practicum.catsgram.dto.ImageUploadResponse;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    // Укажите директорию для хранения изображений
    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    public List<ImageUploadResponse> saveImages(long postId, List<MultipartFile> files) {
        return files.stream().map(file -> saveImage(postId, file)).collect(Collectors.toList());
    }

    public ImageUploadResponse saveImage(long postId, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));

        // Сохраняем изображение на диск и возвращаем путь к файлу
        Path filePath = saveFile(file, post);

        Image image = imageRepository.save(ImageMapper.mapToImage(postId, filePath, file.getOriginalFilename()));

        return ImageMapper.mapToImageUploadResponse(image);
    }

    public List<ImageDTO> getImages(long postId) {
        return imageRepository.findByPostId(postId)
                .stream()
                .map(image -> {
                    byte[] bytes = loadFile(image);
                    return ImageMapper.mapToImageDTO(image, bytes);
                }).collect(Collectors.toList());
    }

    // Загружаем данные указанного изображения с диска
    public ImageData getImageData(long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Изображение с id = " + imageId + " не найдено"));

        // Загрузка файла с диска
        byte[] data = loadFile(image);

        return new ImageData(data, image.getOriginalFileName());
    }

    private Path saveFile(MultipartFile file, Post post) {
        try {
            // Формирование уникального имени файла на основе текущего времени и расширения оригинального файла
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            // Формирование пути для сохранения файла с учетом идентификаторов автора и поста
            Path uploadPath = Path.of(
                    imageDirectory,
                    String.valueOf(post.getAuthor().getId()),
                    String.valueOf(post.getId())
            );

            Path filePath = uploadPath.resolve(uniqueFileName);

            // Создаём директории, если они еще не созданы
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Сохраняем файл по сформированному пути
            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] loadFile(Image image) {
        Path path = Path.of(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName() + e.getMessage());
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }
}
