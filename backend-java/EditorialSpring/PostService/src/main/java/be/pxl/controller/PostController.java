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

    private boolean isAuthorized(String authorizationHeader) {
        return "editor".equals(authorizationHeader);
    }

    @GetMapping("/published")
    public ResponseEntity<List<PublishedPostResponse>> getPublishedPosts() {
        List<PublishedPostResponse> response = postService.getPublishedPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPosts(@RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostResponse> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/openForReview")
    public ResponseEntity<List<PostResponse>> getPostsToReview(@RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostResponse> response = postService.getPostsToReview();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> addPost(@RequestBody PostRequest postRequest, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        postService.addPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/changeContent")
    public ResponseEntity<PostResponse> changeContent(@RequestBody ChangeContentRequest changeContentRequest, @PathVariable Long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            PostResponse response = postService.changeContent(id, changeContentRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}/add-to-review")
    public ResponseEntity<Void> addToReview(@PathVariable Long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            postService.addToReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
