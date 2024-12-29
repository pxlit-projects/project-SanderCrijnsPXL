package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import be.pxl.domain.request.RabbitPostRequest;
import be.pxl.domain.response.RabbitPostResponse;
import be.pxl.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

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

    private Post mapRabbitPostRequestToPost(RabbitPostRequest request) {
        return Post.builder()
                .id(request.id())
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
