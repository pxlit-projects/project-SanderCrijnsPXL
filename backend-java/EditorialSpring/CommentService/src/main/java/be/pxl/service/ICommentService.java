package be.pxl.service;

import be.pxl.domain.request.*;
import be.pxl.domain.response.*;

import java.util.List;

public interface ICommentService {
    void addCommentToPost(Long id, CommentRequest comment);

    List<CommentFeignResponse> getCommentsForPost(Long id);

    void deleteComment(Long id);

    CommentResponse editComment(Long id, EditCommentRequest comment);
}
