package be.pxl.service;

import be.pxl.client.CommentClient;
import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.request.RabbitReviewPostRequest;
import be.pxl.domain.response.CommentResponse;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;
import be.pxl.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CommentClient commentClient;

    @InjectMocks
    private PostService postService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Author")
                .dateCreated(LocalDate.now())
                .status(PostStatus.REVIEW)
                .build();
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

        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(postRequest.title(), savedPost.getTitle());
        assertEquals(postRequest.content(), savedPost.getContent());
        assertEquals(postRequest.author(), savedPost.getAuthor());
        assertEquals(postRequest.status(), savedPost.getStatus());
    }

    @Test
    void changeContent_ShouldUpdatePostContent() {
        ChangeContentRequest changeContentRequest = ChangeContentRequest.builder()
                .title("New Title")
                .content("New Content")
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostResponse response = postService.changeContent(1L, changeContentRequest);

        verify(postRepository).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();
        assertEquals(changeContentRequest.title(), updatedPost.getTitle());
        assertEquals(changeContentRequest.content(), updatedPost.getContent());
        assertEquals(post.getId(), response.id());
        assertEquals(changeContentRequest.title(), response.title());
        assertEquals(changeContentRequest.content(), response.content());
    }

    @Test
    void getPublishedPosts_ShouldReturnPublishedPosts() {
        post.setStatus(PostStatus.PUBLISHED);
        List<Post> posts = List.of(post);
        List<CommentResponse> comments = List.of();
        when(postRepository.findAll()).thenReturn(posts);
        when(commentClient.getCommentsForPost(post.getId())).thenReturn(comments);

        List<PublishedPostResponse> publishedPosts = postService.getPublishedPosts();

        assertEquals(1, publishedPosts.size());
        PublishedPostResponse response = publishedPosts.get(0);
        assertEquals(post.getId(), response.id());
        assertEquals(post.getTitle(), response.title());
        assertEquals(post.getContent(), response.content());
        assertEquals(post.getAuthor(), response.author());
        assertEquals(comments, response.comments());
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        List<Post> posts = List.of(post);
        when(postRepository.findAll()).thenReturn(posts);

        List<PostResponse> allPosts = postService.getAllPosts();

        assertEquals(1, allPosts.size());
        PostResponse response = allPosts.get(0);
        assertEquals(post.getId(), response.id());
        assertEquals(post.getTitle(), response.title());
        assertEquals(post.getContent(), response.content());
        assertEquals(post.getAuthor(), response.author());
    }

    @Test
    void getPostsToReview_ShouldReturnPostsToReview() {
        post.setStatus(PostStatus.REVIEW);
        List<Post> posts = List.of(post);
        when(postRepository.findAll()).thenReturn(posts);

        List<PostResponse> postsToReview = postService.getPostsToReview();

        assertEquals(1, postsToReview.size());
        PostResponse response = postsToReview.get(0);
        assertEquals(post.getId(), response.id());
        assertEquals(post.getTitle(), response.title());
        assertEquals(post.getContent(), response.content());
        assertEquals(post.getAuthor(), response.author());
    }

    @Test
    void addToReview_ShouldUpdatePostStatusToReview() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.addToReview(1L);

        verify(postRepository).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();
        assertEquals(PostStatus.REVIEW, updatedPost.getStatus());
    }

    @Test
    void receivePost_ShouldUpdatePostStatusToPublished() {
        RabbitReviewPostRequest request = new RabbitReviewPostRequest(1L, "ACCEPTED");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.receivePost(request);

        verify(postRepository).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();
        assertEquals(PostStatus.PUBLISHED, updatedPost.getStatus());
    }
}
