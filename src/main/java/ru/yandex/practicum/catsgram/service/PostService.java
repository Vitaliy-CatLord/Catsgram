package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDTO;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    private final Comparator<Post> postDateComparator = Comparator.comparing(Post::getPostDate);

    public PostDTO createPost(NewPostRequest newPostRequest) {
        User author = userRepository.findById(newPostRequest.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Указанный автор не найден"));

        Post post = PostMapper.mapToPost(newPostRequest, author);
        postRepository.save(post);

        return PostMapper.mapToPostDTO(post);
    }

    public List<PostDTO> getPosts(SortOrder sortOrder, int from, int size) {
        if (sortOrder == null) {
            throw new ParameterNotValidException("sortOrder", "Получено: " + sortOrder + " должно быть: asc или desc");
        }
        if (!(size > 0)) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "параметр from не может быть меньше нуля.");
        }
        return postRepository.findAll()
                .stream()
                .sorted(sortOrder.equals(SortOrder.ASCENDING) ?
                        postDateComparator : postDateComparator.reversed())
                .skip(from)
                .limit(size)
                .map(PostMapper::mapToPostDTO)
                .toList();
    }

    public PostDTO getPostById(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден"));

        List<Image> images = imageRepository.findByPostId(postId);
        post.setImages(images);

        return PostMapper.mapToPostDTO(post);
    }

    public PostDTO updatePost(long postId, UpdatePostRequest updateRequest) {
        if (updateRequest.getDescription() == null || updateRequest.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Текст поста не моет быть пуст");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден"));
        post.setDescription(updateRequest.getDescription());
        post.setPostDate(Instant.now());

        postRepository.update(post);
        return PostMapper.mapToPostDTO(post);
    }
}