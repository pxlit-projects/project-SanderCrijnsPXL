package be.pxl.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConfigTest {

    @InjectMocks
    private RabbitMQConfig rabbitMQConfig;

    @Test
    void postToReviewQueue_ShouldCreateQueue() {
        Queue queue = rabbitMQConfig.postToReviewQueue();
        assertNotNull(queue);
        assertEquals("post-to-review-queue", queue.getName());
        assertFalse(queue.isDurable());
    }

    @Test
    void reviewToPostQueue_ShouldCreateQueue() {
        Queue queue = rabbitMQConfig.reviewToPostQueue();
        assertNotNull(queue);
        assertEquals("review-to-post-queue", queue.getName());
        assertFalse(queue.isDurable());
    }

    @Test
    void jsonMessageConverter_ShouldReturnJacksonMessageConverter() {
        MessageConverter converter = rabbitMQConfig.jsonMessageConverter();
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }
}
