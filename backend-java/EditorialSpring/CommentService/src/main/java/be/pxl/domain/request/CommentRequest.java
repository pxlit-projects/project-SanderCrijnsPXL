package be.pxl.domain.request;

import lombok.Builder;

@Builder
public record CommentRequest(String content, String author) {
}
