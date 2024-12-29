package be.pxl.service;

import be.pxl.domain.response.PostResponse;

import java.util.List;

public interface IReviewService {
    void approvePost(Long id);

    void rejectPost(Long id, String comment);

    List<PostResponse> getPostsToReview();
}
