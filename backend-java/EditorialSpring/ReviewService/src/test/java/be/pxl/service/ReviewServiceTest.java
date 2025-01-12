package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import be.pxl.domain.request.RabbitPostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.RabbitPostResponse;
import be.pxl.repository.ReviewRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class ReviewServiceTest {

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    @Autowired
    private ReviewRepository reviewRepository; // the real repository

    @Autowired
    private ReviewService reviewService;       // the real service

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JavaMailSender mailSender;

    private RabbitPostRequest postRequest;
    private Post post;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
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

        List<Post> allPosts = reviewRepository.findAll();
        assertEquals(1, allPosts.size());
        Post savedPost = allPosts.get(0);

        assertEquals(postRequest.id(), savedPost.getId());
        assertEquals(ReviewStatus.PENDING, savedPost.getStatus());
    }

    @Test
    void approvePost_ShouldThrowExceptionIfPostNotFound() {
        // No post saved to DB, so it won't be found
        assertThrows(Exception.class, () -> reviewService.approvePost(1L));
        // We can also assert that DB remains empty
        assertTrue(reviewRepository.findAll().isEmpty());
    }

    @Test
    void approvePost_ShouldUpdateStatusToAcceptedAndSave() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Save the post to DB first
        reviewRepository.save(post);

        reviewService.approvePost(post.getId());

        Post updatedPost = reviewRepository.findById(post.getId()).orElseThrow();
        assertEquals(ReviewStatus.ACCEPTED, updatedPost.getStatus());
    }

    @Test
    void approvePost_ShouldSendPostToRabbitMQ() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        reviewRepository.save(post);

        reviewService.approvePost(post.getId());

        verify(rabbitTemplate).convertAndSend(eq("review-to-post-queue"), any(RabbitPostResponse.class));
    }

    @Test
    void approvePost_ShouldSendEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        reviewRepository.save(post);

        reviewService.approvePost(post.getId());

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void getPostsToReview_ShouldReturnPendingAndRejectedPosts() {
        // We'll save a post with PENDING status
        reviewRepository.save(post); // PENDING by default

        List<PostResponse> responses = reviewService.getPostsToReview();
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(post.getTitle(), responses.get(0).title());
    }
}

