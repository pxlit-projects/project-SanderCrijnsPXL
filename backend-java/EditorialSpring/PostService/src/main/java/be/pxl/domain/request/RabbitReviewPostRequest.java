package be.pxl.domain.request;

import java.io.Serializable;

public record RabbitReviewPostRequest(Long id, String status) implements Serializable {
}
