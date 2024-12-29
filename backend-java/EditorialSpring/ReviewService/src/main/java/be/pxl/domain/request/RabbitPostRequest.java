package be.pxl.domain.request;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record RabbitPostRequest(Long id) implements Serializable {
}
