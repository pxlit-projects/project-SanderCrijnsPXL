package be.pxl.domain.response;

import be.pxl.domain.PostStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PublishedPostResponse(Long id, String title, String content, String author, LocalDate dateCreated, List<CommentResponse> comments) {
}
