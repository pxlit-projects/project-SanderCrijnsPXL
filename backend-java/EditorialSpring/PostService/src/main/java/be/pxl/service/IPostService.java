package be.pxl.service;

import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;
import be.pxl.domain.response.PublishedPostResponse;

import java.util.List;

public interface IPostService {
    void addPost(PostRequest postRequest);

    PostResponse changeContent(Long id, ChangeContentRequest changeContentRequest);

    List<PublishedPostResponse> getPublishedPosts();

    void addToReview(Long id);

    List<PostResponse> getAllPosts();

    List<PostResponse> getPostsToReview();
}
