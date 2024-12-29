package be.pxl.controller;

import be.pxl.domain.response.PostResponse;
import be.pxl.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping("/posts-to-review")
    public List<PostResponse> getPostsToReview() {
        return reviewService.getPostsToReview();
    }

    @PostMapping("/approve/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void approvePost(@PathVariable Long id) {
        reviewService.approvePost(id);
    }

    @PostMapping("/reject/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void rejectPost(@PathVariable Long id, @RequestBody String comment) {
        reviewService.rejectPost(id, comment);
    }
}
