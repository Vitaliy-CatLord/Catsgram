package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.*;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public List<PostDTO> findAll(@RequestParam (defaultValue = "desc") String sort,
                                 @RequestParam (defaultValue = "0") int from,
                                 @RequestParam (defaultValue = "10") int size) {


        return postService.getPosts(SortOrder.from(sort), from, size);
    }

    @GetMapping("/{postId}")
    public PostDTO findPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO create(@RequestBody NewPostRequest createRequest) {

        return postService.createPost(createRequest);
    }

    @PutMapping("/{postId}")
    public PostDTO update(@PathVariable("postId") long postId, @RequestBody UpdatePostRequest updateRequest) {
        return postService.updatePost(postId, updateRequest);
    }
}