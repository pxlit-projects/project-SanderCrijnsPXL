package be.pxl.domain.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RabbitPostResponse (Long id) implements Serializable {
}
