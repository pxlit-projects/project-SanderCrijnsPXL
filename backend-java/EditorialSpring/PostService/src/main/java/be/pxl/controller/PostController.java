package be.pxl.controller;

import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;
import be.pxl.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {
    private final IPostService postService;

    @GetMapping("/published")
    public ResponseEntity<List<PublishedPostResponse>> getPublishedPosts() {
        List<PublishedPostResponse> response = postService.getPublishedPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/openForReview")
    public ResponseEntity<List<PostResponse>> getPostsToReview() {
        List<PostResponse> response = postService.getPostsToReview();
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
    @PatchMapping("/{id}/add-to-review")
    public ResponseEntity<Void> addToReview(@PathVariable Long id) {
        try {
            postService.addToReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
