package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import be.pxl.domain.request.RabbitPostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.RabbitPostResponse;
import be.pxl.repository.ReviewRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ReviewService reviewService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private RabbitPostRequest postRequest;
    private Post post;

    @BeforeEach
    void setUp() {
        postRequest = new RabbitPostRequest(1L, "Test Title", "Test Content", "Author", null);
        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .status(ReviewStatus.PENDING)
                .build();
    }

    @Test
    void receivePost_ShouldSavePostToReview() {
        reviewService.receivePost(postRequest);

        verify(reviewRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertEquals(postRequest.id(), savedPost.getId());
        assertEquals(ReviewStatus.PENDING, savedPost.getStatus());
    }

    @Test
    void approvePost_ShouldThrowExceptionIfPostNotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> reviewService.approvePost(1L));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void approvePost_ShouldUpdateStatusToAcceptedAndSave() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(post));

        reviewService.approvePost(1L);

        verify(reviewRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(ReviewStatus.ACCEPTED, savedPost.getStatus());
    }

    @Test
    void approvePost_ShouldSendPostToRabbitMQ() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(post));

        reviewService.approvePost(1L);

        verify(rabbitTemplate).convertAndSend(eq("review-to-post-queue"), any(RabbitPostResponse.class));
    }

    @Test
    void approvePost_ShouldSendEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(post));

        reviewService.approvePost(1L);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void getPostsToReview_ShouldReturnPendingAndRejectedPosts() {
        when(reviewRepository.findAllByStatus(ReviewStatus.PENDING)).thenReturn(List.of(post));
        when(reviewRepository.findAllByStatus(ReviewStatus.REJECTED)).thenReturn(List.of());

        List<PostResponse> responses = reviewService.getPostsToReview();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(post.getTitle(), responses.get(0).title());
    }
}

