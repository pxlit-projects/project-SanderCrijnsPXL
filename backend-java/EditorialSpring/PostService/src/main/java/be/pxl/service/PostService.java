package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.PostStatus;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;


    @Override
    public void addPost(PostRequest postRequest) {
        postRepository.save(mapToPost(postRequest));
        if (postRequest.status() == PostStatus.REVIEW) {
            //TODO: RabitMQ to ReviewService
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
    public List<PostResponse> getPublishedPosts() {
        List<Post> posts = postRepository.findAll();
        posts = posts.stream().filter(post -> post.getStatus() == PostStatus.PUBLISHED).toList();
        return posts.stream().map(this::mapToPostResponse).toList();
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

        //TODO: RabitMQ to ReviewService
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
}
