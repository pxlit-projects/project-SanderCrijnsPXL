package be.pxl.controller;

import be.pxl.domain.request.PostRequest;
import be.pxl.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;

    @PostMapping
    public void addPost(@RequestBody PostRequest postRequest) {
        postService.addPost(postRequest);
    }
}
