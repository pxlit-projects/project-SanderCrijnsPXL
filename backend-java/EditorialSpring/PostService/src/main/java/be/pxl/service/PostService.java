package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;


    @Override
    public void addPost(PostRequest postRequest) {
        postRepository.save(mapToPost(postRequest));
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
        posts = posts.stream().filter(Post::getIsPublished).toList();
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    private Post mapToPost(PostRequest postRequest) {
        return Post.builder()
                .title(postRequest.title())
                .author(postRequest.author())
                .content(postRequest.content())
                .dateCreated(LocalDate.now())
                .isConcept(postRequest.isConcept())
                .isPublished(false)
                .build();
    }
    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .title(post.getTitle())
                .author(post.getAuthor())
                .content(post.getContent())
                .dateCreated(post.getDateCreated())
                .isConcept(post.getIsConcept())
                .isPublished(post.getIsPublished())
                .build();
    }
}
