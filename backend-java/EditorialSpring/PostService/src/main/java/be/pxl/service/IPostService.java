package be.pxl.service;

import be.pxl.domain.request.ChangeContentRequest;
import be.pxl.domain.request.PostRequest;
import be.pxl.domain.response.PostResponse;

import java.util.List;

public interface IPostService {
    void addPost(PostRequest postRequest);

    PostResponse changeContent(Long id, ChangeContentRequest changeContentRequest);

    List<PostResponse> getPublishedPosts();
}
