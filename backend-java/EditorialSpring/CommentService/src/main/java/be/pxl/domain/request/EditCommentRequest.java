package be.pxl.domain.request;

import lombok.Builder;

@Builder
public record EditCommentRequest(String content) {
}
