package be.pxl.controller;

import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;
import be.pxl.repository.PostRepository;
import be.pxl.service.IPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PostControllerTest {

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
    private IPostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        reset(postService);
    }

    @Test
    void getPublishedPosts_ShouldReturnPublishedPosts() throws Exception {
        Post post = postRepository.save(Post.builder()
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.PUBLISHED)
                .build());

        List<PublishedPostResponse> posts = List.of(PublishedPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .dateCreated(post.getDateCreated())
                .comments(Collections.emptyList())
                .build());

        when(postService.getPublishedPosts()).thenReturn(posts);

        mockMvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    void getPublishedPosts_ShouldReturnEmptyList_WhenNoPostsAvailable() throws Exception {
        when(postService.getPublishedPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void getAllPosts_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/posts/all")
                        .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());

        verify(postService, never()).getAllPosts();
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts_WhenAuthorized() throws Exception {
        List<PostResponse> posts = List.of(PostResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/api/posts/all")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    void addPost_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        PostRequest postRequest = new PostRequest("Test Title", "Test Content", "Author", PostStatus.REVIEW);

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isUnauthorized());

        verify(postService, never()).addPost(any());
    }

    @Test
    void changeContent_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        ChangeContentRequest changeContentRequest = new ChangeContentRequest("New Title", "New Content");

        mockMvc.perform(patch("/api/posts/1/changeContent")
                        .header("Authorization", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeContentRequest)))
                .andExpect(status().isUnauthorized());

        verify(postService, never()).changeContent(anyLong(), any());
    }

    @Test
    void changeContent_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        ChangeContentRequest changeContentRequest = new ChangeContentRequest("New Title", "New Content");
        when(postService.changeContent(anyLong(), any())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(patch("/api/posts/1/changeContent")
                        .header("Authorization", "editor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeContentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addToReview_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(patch("/api/posts/1/add-to-review")
                        .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());

        verify(postService, never()).addToReview(anyLong());
    }

    @Test
    void addToReview_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        doThrow(new RuntimeException("Not found")).when(postService).addToReview(anyLong());

        mockMvc.perform(patch("/api/posts/1/add-to-review")
                        .header("Authorization", "editor"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addToReview_ShouldUpdatePostStatus_WhenAuthorized() throws Exception {
        doNothing().when(postService).addToReview(anyLong());

        mockMvc.perform(patch("/api/posts/1/add-to-review")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk());

        verify(postService, times(1)).addToReview(anyLong());
    }

    @Test
    void getPostsToReview_ShouldReturnUnauthorized_WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/posts/openForReview")
                        .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());

        verify(postService, never()).getPostsToReview();
    }

    @Test
    void getPostsToReview_ShouldReturnPostsToReview_WhenAuthorized() throws Exception {
        List<PostResponse> posts = List.of(PostResponse.builder()
                .id(1L)
                .title("Review Title")
                .content("Review Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        when(postService.getPostsToReview()).thenReturn(posts);

        mockMvc.perform(get("/api/posts/openForReview")
                        .header("Authorization", "editor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Review Title"));
    }
}
