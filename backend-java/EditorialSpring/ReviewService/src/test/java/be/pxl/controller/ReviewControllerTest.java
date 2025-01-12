package be.pxl.controller;

import be.pxl.domain.response.PostResponse;
import be.pxl.service.IReviewService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewController.class)
@Testcontainers
class ReviewControllerTest {

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
    private IReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(reviewService);
    }

    // ✅ TESTING `GET /api/review/posts-to-review`
    @Test
    void getPostsToReview_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/review/posts-to-review")
                        .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());

        verify(reviewService, never()).getPostsToReview();
    }

    @Test
    void getPostsToReview_ShouldReturnPosts_WhenAuthorized() throws Exception {
        List<PostResponse> posts = List.of(PostResponse.builder()
                .id(1L)
                .title("Review Title")
                .content("Review Content")
                .author("Author")
                .build());

        when(reviewService.getPostsToReview()).thenReturn(posts);

        mockMvc.perform(get("/api/review/posts-to-review")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Review Title"));
    }

    @Test
    void getPostsToReview_ShouldReturnEmptyList_WhenNoPostsAvailable() throws Exception {
        when(reviewService.getPostsToReview()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/posts-to-review")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void approvePost_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(post("/api/review/approve/1")
                        .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());

        verify(reviewService, never()).approvePost(anyLong());
    }

    @Test
    void approvePost_ShouldApprovePost_WhenAuthorized() throws Exception {
        doNothing().when(reviewService).approvePost(1L);

        mockMvc.perform(post("/api/review/approve/1")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).approvePost(1L);
    }

    @Test
    void rejectPost_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(post("/api/review/reject/1")
                        .header("Authorization", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid content\""))
                .andExpect(status().isUnauthorized());

        verify(reviewService, never()).rejectPost(anyLong(), anyString());
    }
}