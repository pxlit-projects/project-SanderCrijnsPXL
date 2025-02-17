package be.pxl.service;

import be.pxl.domain.Comment;
import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentFeignResponse;
import be.pxl.domain.response.CommentResponse;
import be.pxl.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    @Override
    public void addCommentToPost(Long id, CommentRequest comment) {
        log.info("Adding comment to post with ID {}", id);
        commentRepository.save(mapCommentRequestToComment(id, comment));
        log.info("Comment successfully added to post with ID {}", id);
    }

    @Override
    public List<CommentFeignResponse> getCommentsForPost(Long id) {
        log.info("Getting comments for post with ID {}", id);
        List<CommentFeignResponse> comments = commentRepository.findAllByPostId(id).stream()
                .map(this::mapCommentToCommentFeignResponse)
                .toList();
        return comments;
    }

    @Override
    public void deleteComment(Long id) {
        log.info("Deleting comment with ID {}", id);
        commentRepository.deleteById(id);
        log.info("Comment with ID {} successfully deleted", id);
    }

    @Override
    public CommentResponse editComment(Long id, EditCommentRequest comment) {
        log.info("Editing comment with ID {}", id);
        Comment commentToEdit = commentRepository.findById(id).orElseThrow();
        commentToEdit.setContent(comment.content());

        commentRepository.save(commentToEdit);
        log.info("Comment with ID {} successfully edited", id);

        return mapCommentToCommentResponse(commentToEdit);
    }

    private Comment mapCommentRequestToComment(Long id, CommentRequest commentRequest) {
         return Comment.builder()
                    .postId(id)
                    .content(commentRequest.content())
                    .author(commentRequest.author())
                    .build();
    }

    private CommentResponse mapCommentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .build();
    }

    private CommentFeignResponse mapCommentToCommentFeignResponse(Comment comment) {
        return CommentFeignResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .build();
    }

}
