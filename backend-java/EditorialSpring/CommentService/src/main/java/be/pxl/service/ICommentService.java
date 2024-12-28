package be.pxl.service;

import be.pxl.domain.request.CommentRequest;
import be.pxl.domain.request.EditCommentRequest;
import be.pxl.domain.response.CommentResponse;

import java.util.List;

public interface ICommentService {
    void addCommentToPost(Long id, CommentRequest comment);

    List<CommentResponse> getCommentsForPost(Long id);

    void deleteComment(Long id);

    CommentResponse editComment(Long id, EditCommentRequest comment);
}
