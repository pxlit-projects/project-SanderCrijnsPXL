package be.pxl.service;

public interface IReviewService {
    void approvePost(Long id);

    void rejectPost(Long id, String comment);
}
