package be.pxl.service;

import be.pxl.client.CommentClient;
import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.*;
import be.pxl.domain.response.*;
import be.pxl.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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

    @RabbitListener(queues = "review-to-post-queue")
    public void receivePost(RabbitReviewPostRequest request) {
        Post post = postRepository.findById(request.id()).orElseThrow();
        if (request.status().equals("ACCEPTED")) {
            post.setStatus(PostStatus.PUBLISHED);
        }
        postRepository.save(post);
    }


    @Override
    public void addPost(PostRequest postRequest) {
        Post post = mapToPost(postRequest);
        postRepository.save(post);
        if (postRequest.status() == PostStatus.REVIEW) {
            sendToReviewService(post);
        }
    }

    @Override
    public PostResponse changeContent(Long id, ChangeContentRequest changeContentRequest) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(changeContentRequest.title());
        post.setContent(changeContentRequest.content());
        postRepository.save(post);
        return mapToPostResponse(post);
    }

    @Override
    public List<PublishedPostResponse> getPublishedPosts() {
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
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getPostsToReview() {
        List<Post> posts = postRepository.findAll();
        posts = posts.stream().filter(post -> post.getStatus() == PostStatus.REVIEW).toList();
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public void addToReview(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setStatus(PostStatus.REVIEW);
        postRepository.save(post);

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
        rabbitTemplate.convertAndSend("post-to-review-queue", RabbitPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .dateCreated(post.getDateCreated().toString())
                .build());
    }
}
