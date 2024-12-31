package be.pxl.domain.response;

import lombok.Builder;

@Builder
public record CommentFeignResponse(Long id, String content, String author) {
}
