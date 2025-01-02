package be.pxl.controller;

import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentFeignResponse;
import be.pxl.domain.response.CommentResponse;
import be.pxl.service.ICommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private ICommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCommentsForPost_ShouldReturnComments() {
        List<CommentFeignResponse> comments = List.of(new CommentFeignResponse(1L, "Test Content", "Author"));
        when(commentService.getCommentsForPost(1L)).thenReturn(comments);

        ResponseEntity<List<CommentFeignResponse>> response = commentController.getCommentsForPost(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
        verify(commentService, times(1)).getCommentsForPost(1L);
    }

    @Test
    void addPost_ShouldAddComment() {
        CommentRequest commentRequest = new CommentRequest("Test Content", "Author");

        commentController.addPost(1L, commentRequest);

        verify(commentService, times(1)).addCommentToPost(1L, commentRequest);
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        commentController.deleteComment(1L);

        verify(commentService, times(1)).deleteComment(1L);
    }

    @Test
    void editComment_ShouldReturnEditedComment() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("New Content");
        CommentResponse commentResponse = new CommentResponse(1L, 1L, "New Content", "Author");
        when(commentService.editComment(1L, editCommentRequest)).thenReturn(commentResponse);

        ResponseEntity<CommentResponse> response = commentController.editComment(1L, editCommentRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentResponse, response.getBody());
        verify(commentService, times(1)).editComment(1L, editCommentRequest);
    }

    @Test
    void editComment_ShouldReturnNotFound_WhenCommentNotFound() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("New Content");
        when(commentService.editComment(1L, editCommentRequest)).thenThrow(new RuntimeException());

        ResponseEntity<CommentResponse> response = commentController.editComment(1L, editCommentRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
