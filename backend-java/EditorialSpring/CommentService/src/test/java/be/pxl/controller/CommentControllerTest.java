package be.pxl.controller;

import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentFeignResponse;
import be.pxl.domain.response.CommentResponse;
import be.pxl.service.ICommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
@Testcontainers
class CommentControllerTest {

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(commentService);
    }

    @Test
    void getCommentsForPost_ShouldReturnComments() throws Exception {
        List<CommentFeignResponse> comments = List.of(new CommentFeignResponse(1L, "Test Content", "Author"));
        when(commentService.getCommentsForPost(1L)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].author").value("Author"));
    }

    @Test
    void getCommentsForPost_ShouldReturnEmptyList_WhenNoCommentsExist() throws Exception {
        when(commentService.getCommentsForPost(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void addPost_ShouldAddComment() throws Exception {
        CommentRequest commentRequest = new CommentRequest("New Comment", "New Author");

        mockMvc.perform(post("/api/comments/add/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());

        verify(commentService, times(1)).addCommentToPost(eq(1L), any(CommentRequest.class));
    }

    @Test
    void deleteComment_ShouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteComment(1L);
    }

    @Test
    void deleteComment_ShouldNotThrowException_WhenCommentDoesNotExist() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete("/api/comments/999"))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteComment(999L);
    }

    @Test
    void editComment_ShouldReturnEditedComment() throws Exception {
        EditCommentRequest editCommentRequest = new EditCommentRequest("Updated Content");
        CommentResponse commentResponse = new CommentResponse(1L, 1L, "Updated Content", "Author");

        when(commentService.editComment(1L, editCommentRequest)).thenReturn(commentResponse);

        mockMvc.perform(patch("/api/comments/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.author").value("Author"));
    }

    @Test
    void editComment_ShouldReturnNotFound_WhenCommentNotFound() throws Exception {
        EditCommentRequest editCommentRequest = new EditCommentRequest("Updated Content");

        when(commentService.editComment(1L, editCommentRequest)).thenThrow(new RuntimeException());

        mockMvc.perform(patch("/api/comments/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editCommentRequest)))
                .andExpect(status().isNotFound());
    }
}
