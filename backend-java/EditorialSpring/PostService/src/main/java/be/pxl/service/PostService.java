package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.request.PostRequest;
import be.pxl.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{
    private final PostRepository postRepository;

    @Override
    public void addPost(PostRequest postRequest) {
        postRepository.save(mapToPost(postRequest));
    }

    private Post mapToPost(PostRequest postRequest) {
        return Post.builder()
                .title(postRequest.title())
                .author(postRequest.author())
                .content(postRequest.content())
                .dateCreated(LocalDate.now())
                .build();
    }
}
