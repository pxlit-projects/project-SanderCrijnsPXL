package be.pxl.domain.response;

import lombok.Builder;

@Builder
public record CommentResponse(Long id, String content, String author) {
}
