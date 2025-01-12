package be.pxl.service;

import be.pxl.domain.Comment;
import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentFeignResponse;
import be.pxl.domain.response.CommentResponse;
import be.pxl.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class CommentServiceTest {

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

        // Create a test comment and save it
        testComment = commentRepository.save(Comment.builder()
                .postId(1L)
                .content("Test Content")
                .author("Test Author")
                .build());
    }

    @Test
    void addCommentToPost_ShouldSaveComment() {
        CommentRequest commentRequest = new CommentRequest("New Comment", "New Author");

        commentService.addCommentToPost(1L, commentRequest);

        List<Comment> savedComments = commentRepository.findAll();
        assertEquals(2, savedComments.size());

        Comment savedComment = savedComments.get(1);
        assertEquals(commentRequest.content(), savedComment.getContent());
        assertEquals(commentRequest.author(), savedComment.getAuthor());
        assertEquals(1L, savedComment.getPostId());
    }

    @Test
    void getCommentsForPost_ShouldReturnComments() {
        List<CommentFeignResponse> comments = commentService.getCommentsForPost(1L);

        assertEquals(1, comments.size());
        CommentFeignResponse response = comments.get(0);
        assertEquals(testComment.getId(), response.id());
        assertEquals(testComment.getContent(), response.content());
        assertEquals(testComment.getAuthor(), response.author());
    }

    @Test
    void getCommentsForPost_ShouldReturnEmptyList_WhenNoCommentsExist() {
        commentRepository.deleteAll();

        List<CommentFeignResponse> comments = commentService.getCommentsForPost(1L);

        assertTrue(comments.isEmpty());
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        commentService.deleteComment(testComment.getId());

        assertFalse(commentRepository.existsById(testComment.getId()));
    }

    @Test
    void deleteComment_ShouldNotThrowException_WhenCommentDoesNotExist() {
        assertDoesNotThrow(() -> commentService.deleteComment(999L));
    }

    @Test
    void editComment_ShouldUpdateCommentContent() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("Updated Content");

        CommentResponse response = commentService.editComment(testComment.getId(), editCommentRequest);

        Comment updatedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertEquals(editCommentRequest.content(), updatedComment.getContent());
        assertEquals(testComment.getId(), response.id());
        assertEquals(editCommentRequest.content(), response.content());
    }

    @Test
    void editComment_ShouldThrowException_WhenCommentNotFound() {
        EditCommentRequest editCommentRequest = new EditCommentRequest("Updated Content");

        assertThrows(RuntimeException.class, () -> commentService.editComment(999L, editCommentRequest));
    }
}
