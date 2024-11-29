package be.pxl.controller;

import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;

    @GetMapping("/publishedPosts")
    public ResponseEntity<List<PostResponse>> getPublishedPosts() {
        List<PostResponse> response = postService.getPublishedPosts();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@RequestBody PostRequest postRequest) {
        postService.addPost(postRequest);
    }

    @PatchMapping("/{id}/changeContent")
    public ResponseEntity<PostResponse> changeContent(@RequestBody ChangeContentRequest changeContentRequest, @PathVariable Long id) {
        try {
            PostResponse response = postService.changeContent(id, changeContentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
