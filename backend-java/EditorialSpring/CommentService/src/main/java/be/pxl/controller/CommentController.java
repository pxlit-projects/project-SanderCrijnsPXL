package be.pxl.controller;

import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentResponse;
import be.pxl.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    private final ICommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable Long postId) {
        List<CommentResponse> response = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@PathVariable Long postId , @RequestBody CommentRequest comment) {
        commentService.addCommentToPost(postId, comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<CommentResponse> editComment(@PathVariable Long id, @RequestBody EditCommentRequest comment) {
        try {
            CommentResponse response = commentService.editComment(id, comment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
