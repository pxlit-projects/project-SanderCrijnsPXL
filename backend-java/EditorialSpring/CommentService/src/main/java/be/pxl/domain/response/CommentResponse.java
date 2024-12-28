package be.pxl.domain.response;

import lombok.Builder;

@Builder
public record CommentResponse(Long id, Long postId, String content, String author) {
}
