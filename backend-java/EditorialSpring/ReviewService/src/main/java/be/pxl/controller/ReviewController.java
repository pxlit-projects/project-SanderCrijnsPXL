package be.pxl.controller;

import be.pxl.domain.response.PostResponse;
import be.pxl.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    private final IReviewService reviewService;

    private boolean isAuthorized(String authorizationHeader) {
        return "editor".equals(authorizationHeader);
    }

    @GetMapping("/posts-to-review")
    public ResponseEntity<List<PostResponse>> getPostsToReview(@RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PostResponse> postsToReview = reviewService.getPostsToReview();
        return ResponseEntity.ok(postsToReview);
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Object> approvePost(@PathVariable Long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.approvePost(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Object> rejectPost(@PathVariable Long id, @RequestBody String comment, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (!isAuthorized(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.rejectPost(id, comment);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
