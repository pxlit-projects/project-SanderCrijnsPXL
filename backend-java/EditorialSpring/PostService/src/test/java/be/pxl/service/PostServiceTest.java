package be.pxl.service;

import be.pxl.client.CommentClient;
import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.request.RabbitReviewPostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;
import be.pxl.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
class PostServiceTest {

    @Container
    private static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
    }

    // We want the REAL PostRepository & PostService from Spring’s context
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    /**
     * If you still want to mock out RabbitMQ or external clients,
     * you can keep them as @MockBean. If you'd like, you could also
     * spin up a Rabbit container, but that's more involved.
     */
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private CommentClient commentClient;

    @BeforeEach
    void cleanDatabase() {
        postRepository.deleteAll();
    }

    @Test
    void addPost_ShouldSavePost() {
        PostRequest postRequest = PostRequest.builder()
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .status(PostStatus.REVIEW)
                .build();

        postService.addPost(postRequest);

        List<Post> allPosts = postRepository.findAll();
        assertEquals(1, allPosts.size());

        Post savedPost = allPosts.get(0);
        assertEquals("Test Title", savedPost.getTitle());
        assertEquals("Test Content", savedPost.getContent());
        assertEquals("Author", savedPost.getAuthor());
        assertEquals(PostStatus.REVIEW, savedPost.getStatus());
    }

    @Test
    void changeContent_ShouldUpdatePostContent() {
        Post post = postRepository.save(Post.builder()
                .title("Old Title")
                .content("Old Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        ChangeContentRequest changeContentRequest = ChangeContentRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();

        PostResponse response = postService.changeContent(post.getId(), changeContentRequest);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Content", updatedPost.getContent());

        assertEquals(post.getId(), response.id());
        assertEquals("New Title", response.title());
        assertEquals("New Content", response.content());
    }

    @Test
    void getPublishedPosts_ShouldReturnPublishedPosts() {
        Post publishedPost = postRepository.save(Post.builder()
                .title("Published Title")
                .content("Published Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.PUBLISHED)
                .build());

        when(commentClient.getCommentsForPost(publishedPost.getId()))
                .thenReturn(List.of()); // mock no comments

        List<PublishedPostResponse> publishedPosts = postService.getPublishedPosts();
        assertEquals(1, publishedPosts.size());

        PublishedPostResponse response = publishedPosts.get(0);
        assertEquals(publishedPost.getId(), response.id());
        assertEquals("Published Title", response.title());
        assertEquals("Published Content", response.content());
        assertEquals("Author", response.author());
        assertTrue(response.comments().isEmpty());
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        Post post = postRepository.save(Post.builder()
                .title("All Title")
                .content("All Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        List<PostResponse> allPosts = postService.getAllPosts();
        assertEquals(1, allPosts.size());

        PostResponse response = allPosts.get(0);
        assertEquals(post.getId(), response.id());
        assertEquals("All Title", response.title());
        assertEquals("All Content", response.content());
        assertEquals("Author", response.author());
    }

    @Test
    void getPostsToReview_ShouldReturnPostsToReview() {
        Post reviewPost = postRepository.save(Post.builder()
                .title("Review Title")
                .content("Review Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        List<PostResponse> postsToReview = postService.getPostsToReview();
        assertEquals(1, postsToReview.size());

        PostResponse response = postsToReview.get(0);
        assertEquals(reviewPost.getId(), response.id());
        assertEquals("Review Title", response.title());
        assertEquals("Review Content", response.content());
        assertEquals("Author", response.author());
    }

    @Test
    void addToReview_ShouldUpdatePostStatusToReview() {
        Post post = postRepository.save(Post.builder()
                .title("Draft Title")
                .content("Draft Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.CONCEPT)
                .build());

        postService.addToReview(post.getId());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(PostStatus.REVIEW, updatedPost.getStatus());
    }

    @Test
    void receivePost_ShouldUpdatePostStatusToPublished() {
        Post post = postRepository.save(Post.builder()
                .title("Review Title")
                .content("Review Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build());

        RabbitReviewPostRequest request = new RabbitReviewPostRequest(post.getId(), "ACCEPTED");

        postService.receivePost(request);

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(PostStatus.PUBLISHED, updatedPost.getStatus());
    }
}
