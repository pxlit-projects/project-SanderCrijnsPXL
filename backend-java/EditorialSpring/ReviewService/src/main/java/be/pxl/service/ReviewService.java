package be.pxl.service;

import be.pxl.domain.Post;
import be.pxl.domain.ReviewStatus;
import be.pxl.domain.request.RabbitPostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.RabbitPostResponse;
import be.pxl.repository.ReviewRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @RabbitListener(queues = "post-to-review-queue")
    public void receivePost(RabbitPostRequest post) {
        log.info("Received post with ID {} for review", post.id());
        reviewRepository.save(mapRabbitPostRequestToPost(post));
        log.info("Post with ID {} successfully saved for review", post.id());
    }

    @Override
    public void approvePost(Long id) {
        log.info("Approving post with ID {}", id);
        Post post = reviewRepository.findById(id).orElseThrow();
        post.setStatus(ReviewStatus.ACCEPTED);
        reviewRepository.save(post);

        rabbitTemplate.convertAndSend("review-to-post-queue", mapPostToRabbitPostResponse(post));

        log.info("Post with ID {} successfully approved", id);
        sendEmail(post);
    }

    @Override
    public void rejectPost(Long id, String comment) {
        log.info("Rejecting post with ID {}", id);
        Post post = reviewRepository.findById(id).orElseThrow();
        post.setStatus(ReviewStatus.REJECTED);
        post.setComment(comment);
        reviewRepository.save(post);

        log.info("Post with ID {} successfully rejected", id);
        sendEmail(post);
    }

    @Override
    public List<PostResponse> getPostsToReview() {
        log.info("Retrieving posts to review");
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

    private void sendEmail(Post post) {
        log.info("Sending email to notify user of post status update");
        String subject = "Post Review Status Update";
        String recipient = "sander.crijns@student.pxl.be";
        String message = String.format("Post with Title %s, ID %d has been %s.", post.getTitle(), post.getId(), post.getStatus().toString().toLowerCase());

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(message, false);

            mailSender.send(mail);
            log.info("Email sent successfully to {}", recipient);
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
}
