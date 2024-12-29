package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import be.pxl.domain.request.RabbitPostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.RabbitPostResponse;
import be.pxl.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "post-to-review-queue")
    public void receivePost(RabbitPostRequest post) {
        reviewRepository.save(mapRabbitPostRequestToPost(post));
    }

    @Override
    public void approvePost(Long id) {
        Post post = reviewRepository.findById(id).orElseThrow();
        post.setStatus(ReviewStatus.ACCEPTED);
        reviewRepository.save(post);

        rabbitTemplate.convertAndSend("review-to-post-queue", mapPostToRabbitPostResponse(post));
    }

    @Override
    public void rejectPost(Long id, String comment) {
        Post post = reviewRepository.findById(id).orElseThrow();
        post.setStatus(ReviewStatus.REJECTED);
        post.setComment(comment);
        reviewRepository.save(post);
    }

    @Override
    public List<PostResponse> getPostsToReview() {
        List<PostResponse> listPending  = reviewRepository.findAllByStatus(ReviewStatus.PENDING).stream()
                .map(this::mapPostToPostResponse)
                .toList();
        List<PostResponse> listRejected = reviewRepository.findAllByStatus(ReviewStatus.REJECTED).stream()
                .map(this::mapPostToPostResponse)
                .toList();
        return List.of(listPending, listRejected).stream()
                .flatMap(List::stream)
                .toList();
    }

    private PostResponse mapPostToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .dateCreated(post.getDateCreated())
                .comment(post.getComment())
                .build();
    }

    private Post mapRabbitPostRequestToPost(RabbitPostRequest request) {
        return Post.builder()
                .id(request.id())
                .title(request.title())
                .content(request.content())
                .author(request.author())
                .dateCreated(request.dateCreated())
                .status(ReviewStatus.PENDING)
                .comment(null)
                .build();
    }

    private RabbitPostResponse mapPostToRabbitPostResponse(Post post) {
        return RabbitPostResponse.builder()
                .id(post.getId())
                .status(post.getStatus().toString())
                .build();
    }
}
