package be.pxl.domain.request;

import lombok.Builder;

@Builder
public record ChangeContentRequest(String title, String content) {}
