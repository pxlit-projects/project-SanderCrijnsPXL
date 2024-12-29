package be.pxl.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabitMQConfig {
    @Bean
    public Queue postToReviewQueue() {
        return new Queue("post-to-review-queue", false);
    }

    @Bean
    public Queue reviewToPostQueue() {
        return new Queue("review-to-post-queue", false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
