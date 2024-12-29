package be.pxl.domain.request;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RabbitPostRequest(Long id, String title, String content, String author, String dateCreated) implements Serializable {
}
