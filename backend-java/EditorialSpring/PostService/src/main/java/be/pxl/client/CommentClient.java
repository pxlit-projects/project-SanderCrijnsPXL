package be.pxl.client;

import be.pxl.domain.response.CommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "CommentService")
public interface CommentClient {

    @GetMapping("/api/comments/{postId}")
    List<CommentResponse> getCommentsForPost(@PathVariable Long postId);

}

