package be.pxl.domain.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RabbitPostResponse (Long id, String title, String content, String author, String dateCreated) implements Serializable {
}
