package be.pxl.controller;

import be.pxl.domain.PostStatus;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;
import be.pxl.service.IPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private IPostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPublishedPosts_ShouldReturnPublishedPosts() {
        List<PublishedPostResponse> posts = List.of(PublishedPostResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .comments(List.of())
                .build());
        when(postService.getPublishedPosts()).thenReturn(posts);

        ResponseEntity<List<PublishedPostResponse>> response = postController.getPublishedPosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
        verify(postService, times(1)).getPublishedPosts();
    }

    @Test
    void getAllPosts_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<List<PostResponse>> response = postController.getAllPosts("invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(postService, never()).getAllPosts();
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts_WhenAuthorized() {
        List<PostResponse> posts = List.of(PostResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.PUBLISHED)
                .build());
        when(postService.getAllPosts()).thenReturn(posts);

        ResponseEntity<List<PostResponse>> response = postController.getAllPosts("editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void getPostsToReview_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<List<PostResponse>> response = postController.getPostsToReview("invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(postService, never()).getPostsToReview();
    }

    @Test
    void getPostsToReview_ShouldReturnPostsToReview_WhenAuthorized() {
        List<PostResponse> posts = List.of(PostResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());
        when(postService.getPostsToReview()).thenReturn(posts);

        ResponseEntity<List<PostResponse>> response = postController.getPostsToReview("editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
        verify(postService, times(1)).getPostsToReview();
    }

    @Test
    void addPost_ShouldReturnUnauthorized_WhenNotAuthorized() {
        PostRequest postRequest = PostRequest.builder()
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .status(PostStatus.REVIEW)
                .build();
        ResponseEntity<Void> response = postController.addPost(postRequest, "invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(postService, never()).addPost(any());
    }

    @Test
    void addPost_ShouldAddPost_WhenAuthorized() {
        PostRequest postRequest = PostRequest.builder()
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .status(PostStatus.REVIEW)
                .build();
        ResponseEntity<Void> response = postController.addPost(postRequest, "editor");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(postService, times(1)).addPost(postRequest);
    }

    @Test
    void changeContent_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ChangeContentRequest changeContentRequest = ChangeContentRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();
        ResponseEntity<PostResponse> response = postController.changeContent(changeContentRequest, 1L, "invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(postService, never()).changeContent(anyLong(), any());
    }

    @Test
    void changeContent_ShouldReturnNotFound_WhenPostNotFound() {
        ChangeContentRequest changeContentRequest = ChangeContentRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();
        when(postService.changeContent(anyLong(), any())).thenThrow(new RuntimeException());

        ResponseEntity<PostResponse> response = postController.changeContent(changeContentRequest, 1L, "editor");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void changeContent_ShouldChangeContent_WhenAuthorized() {
        ChangeContentRequest changeContentRequest = ChangeContentRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();
        PostResponse postResponse = PostResponse.builder()
                .id(1L)
                .title("New Title")
                .content("New Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.PUBLISHED)
                .build();
        when(postService.changeContent(anyLong(), any())).thenReturn(postResponse);

        ResponseEntity<PostResponse> response = postController.changeContent(changeContentRequest, 1L, "editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postResponse, response.getBody());
        verify(postService, times(1)).changeContent(1L, changeContentRequest);
    }

    @Test
    void addToReview_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<Void> response = postController.addToReview(1L, "invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(postService, never()).addToReview(anyLong());
    }

    @Test
    void addToReview_ShouldReturnNotFound_WhenPostNotFound() {
        doThrow(new RuntimeException()).when(postService).addToReview(anyLong());

        ResponseEntity<Void> response = postController.addToReview(1L, "editor");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addToReview_ShouldAddToReview_WhenAuthorized() {
        ResponseEntity<Void> response = postController.addToReview(1L, "editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postService, times(1)).addToReview(1L);
    }
}
