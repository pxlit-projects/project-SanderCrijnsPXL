package be.pxl.domain.response;

import be.pxl.domain.PostStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PostResponse(Long id, String title, String content, String author, LocalDate dateCreated, PostStatus status) {
}
