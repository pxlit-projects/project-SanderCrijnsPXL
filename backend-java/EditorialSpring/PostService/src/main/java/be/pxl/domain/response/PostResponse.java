package be.pxl.domain.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PostResponse(String title, String content, String author, LocalDate dateCreated, Boolean isConcept, Boolean isPublished) {
}
