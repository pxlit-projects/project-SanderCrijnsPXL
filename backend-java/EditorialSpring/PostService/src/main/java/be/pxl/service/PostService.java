package be.pxl.service;

import be.pxl.client.CommentClient;
import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.*;
import be.pxl.domain.response.*;
import be.pxl.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CommentClient commentClient;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @RabbitListener(queues = "review-to-post-queue")
    public void receivePost(RabbitReviewPostRequest request) {
        log.info("Received post with ID {} for review", request.id());
        Post post = postRepository.findById(request.id()).orElseThrow();
        if (request.status().equals("ACCEPTED")) {
            post.setStatus(PostStatus.PUBLISHED);
        }
        postRepository.save(post);
        log.info("Post with ID {} successfully saved", request.id());
    }


    @Override
    public void addPost(PostRequest postRequest) {
        log.info("Adding post");
        Post post = mapToPost(postRequest);
        postRepository.save(post);
        log.info("Post successfully added");
        if (postRequest.status() == PostStatus.REVIEW) {
            sendToReviewService(post);
        }
    }

    @Override
    public PostResponse changeContent(Long id, ChangeContentRequest changeContentRequest) {
        log.info("Changing content of post with ID {}", id);
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(changeContentRequest.title());
        post.setContent(changeContentRequest.content());
        postRepository.save(post);
        log.info("Content of post with ID {} successfully changed", id);
        return mapToPostResponse(post);
    }

    @Override
    public List<PublishedPostResponse> getPublishedPosts() {
        log.info("Retrieving published posts");
        List<Post> posts = postRepository.findAll();
        posts = posts.stream().filter(post -> post.getStatus() == PostStatus.PUBLISHED).toList();
        List<PublishedPostResponse> publishedPosts = posts.stream().map(post -> {
            List<CommentResponse> comments = commentClient.getCommentsForPost(post.getId());
            return mapToPublishedPostResponse(post, comments);
        }).toList();
        return publishedPosts;
    }

    @Override
    public List<PostResponse> getAllPosts() {
        log.info("Retrieving all posts");
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getPostsToReview() {
        log.info("Retrieving posts to review");
        List<Post> posts = postRepository.findAll();
        posts = posts.stream().filter(post -> post.getStatus() == PostStatus.REVIEW).toList();
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public void addToReview(Long id) {
        log.info("Adding post with ID {} to review", id);
        Post post = postRepository.findById(id).orElseThrow();
        post.setStatus(PostStatus.REVIEW);
        postRepository.save(post);
        log.info("Post with ID {} successfully added to review", id);

        sendToReviewService(post);
    }


    private Post mapToPost(PostRequest postRequest) {
        return Post.builder()
                .title(postRequest.title())
                .author(postRequest.author())
                .content(postRequest.content())
                .dateCreated(LocalDate.now())
                .status(postRequest.status())
                .build();
    }
    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .content(post.getContent())
                .dateCreated(post.getDateCreated())
                .status(post.getStatus())
                .build();
    }

    private PublishedPostResponse mapToPublishedPostResponse(Post post, List<CommentResponse> comments) {
        return PublishedPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .content(post.getContent())
                .dateCreated(post.getDateCreated())
                .comments(comments)
                .build();
    }
    private void sendToReviewService(Post post) {
        log.info("Sending post with ID {} to review service", post.getId());
        rabbitTemplate.convertAndSend("post-to-review-queue", RabbitPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .dateCreated(post.getDateCreated().toString())
                .build());
        log.info("Post with ID {} successfully sent to review service", post.getId());
    }
}
