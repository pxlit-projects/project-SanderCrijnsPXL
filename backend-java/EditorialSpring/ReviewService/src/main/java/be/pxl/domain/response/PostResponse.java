package be.pxl.domain.response;

import lombok.Builder;

@Builder
public record PostResponse(Long id, String title, String content, String author, String dateCreated, String comment) {
}
