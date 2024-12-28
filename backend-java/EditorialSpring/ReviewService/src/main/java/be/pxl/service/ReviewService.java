package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;

    //TODO: RabitMQ from PostService

    @Override
    public void approvePost(Long id) {
       Post post = reviewRepository.findById(id).orElseThrow();
       post.setIsAccepted(true);
       reviewRepository.save(post);

        //TODO: RabitMQ to PostService
    }

    @Override
    public void rejectPost(Long id, String comment) {
        Post post = reviewRepository.findById(id).orElseThrow();
        post.setIsAccepted(false);
        post.setComment(comment);
        reviewRepository.save(post);

        //TODO: RabitMQ to PostService
    }
}
