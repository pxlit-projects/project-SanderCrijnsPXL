package be.pxl.service;

import be.pxl.domain.Comment;
import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentFeignResponse;
import be.pxl.domain.response.CommentResponse;
import be.pxl.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        comment = Comment.builder()
                .id(1L)
                .postId(1L)
                .content("Test Content")
                .author("Author")
                .build();
    }

    @Test
    void addCommentToPost_ShouldSaveComment() {
        CommentRequest commentRequest = new CommentRequest("Test Content", "Author");

        commentService.addCommentToPost(1L, commentRequest);

        verify(commentRepository).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertEquals(commentRequest.content(), savedComment.getContent());
        assertEquals(commentRequest.author(), savedComment.getAuthor());
        assertEquals(1L, savedComment.getPostId());
    }

    @Test
    void getCommentsForPost_ShouldReturnComments() {
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(1L)).thenReturn(comments);

        List<CommentFeignResponse> response = commentService.getCommentsForPost(1L);

        assertEquals(1, response.size());
        CommentFeignResponse commentResponse = response.get(0);
        assertEquals(comment.getId(), commentResponse.id());
        assertEquals(comment.getContent(), commentResponse.content());
        assertEquals(comment.getAuthor(), commentResponse.author());
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void editComment_ShouldUpdateCommentContent() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("New Content");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.editComment(1L, editCommentRequest);

        verify(commentRepository).save(commentCaptor.capture());
        Comment updatedComment = commentCaptor.getValue();
        assertEquals(editCommentRequest.content(), updatedComment.getContent());
        assertEquals(comment.getId(), response.id());
        assertEquals(editCommentRequest.content(), response.content());
    }

    @Test
    void editComment_ShouldThrowException_WhenCommentNotFound() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("New Content");
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.editComment(1L, editCommentRequest));
    }
}
