package be.pxl.controller;

import be.pxl.domain.response.PostResponse;
import be.pxl.service.IReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private IReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostsToReview_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<List<PostResponse>> response = reviewController.getPostsToReview("invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(reviewService, never()).getPostsToReview();
    }

//    @Test
//    void getPostsToReview_ShouldReturnPosts_WhenAuthorized() {
//        List<PostResponse> posts = List.of(new PostResponse().builder().build());
//        when(reviewService.getPostsToReview()).thenReturn(posts);
//
//        ResponseEntity<List<PostResponse>> response = reviewController.getPostsToReview("editor");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(posts, response.getBody());
//        verify(reviewService, times(1)).getPostsToReview();
//    }

    @Test
    void approvePost_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<Object> response = reviewController.approvePost(1L, "invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(reviewService, never()).approvePost(anyLong());
    }

    @Test
    void approvePost_ShouldApprovePost_WhenAuthorized() {
        ResponseEntity<Object> response = reviewController.approvePost(1L, "editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService, times(1)).approvePost(1L);
    }

    @Test
    void rejectPost_ShouldReturnUnauthorized_WhenNotAuthorized() {
        ResponseEntity<Object> response = reviewController.rejectPost(1L, "comment", "invalid");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(reviewService, never()).rejectPost(anyLong(), anyString());
    }

    @Test
    void rejectPost_ShouldRejectPost_WhenAuthorized() {
        ResponseEntity<Object> response = reviewController.rejectPost(1L, "comment", "editor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reviewService, times(1)).rejectPost(1L, "comment");
    }
}