package be.pxl.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MailConfigTest {

    @InjectMocks
    private MailConfig mailConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailConfig, "host", "smtp.test.com");
        ReflectionTestUtils.setField(mailConfig, "port", 587);
        ReflectionTestUtils.setField(mailConfig, "username", "testuser");
        ReflectionTestUtils.setField(mailConfig, "password", "testpass");
    }

    @Test
    void javaMailSender_ShouldReturnConfiguredJavaMailSender() {
        JavaMailSender mailSender = mailConfig.javaMailSender();

        assertNotNull(mailSender);
        assertInstanceOf(JavaMailSenderImpl.class, mailSender);

        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
        assertEquals("smtp.test.com", mailSenderImpl.getHost());
        assertEquals(587, mailSenderImpl.getPort());
        assertEquals("testuser", mailSenderImpl.getUsername());
        assertEquals("testpass", mailSenderImpl.getPassword());

        Properties mailProps = mailSenderImpl.getJavaMailProperties();
        assertEquals("smtp", mailProps.getProperty("mail.transport.protocol"));
        assertEquals("true", mailProps.getProperty("mail.smtp.auth"));
        assertEquals("true", mailProps.getProperty("mail.smtp.starttls.enable"));
        assertEquals("true", mailProps.getProperty("mail.smtp.starttls.required"));
        assertEquals("5000", mailProps.getProperty("mail.smtp.timeout"));
        assertEquals("5000", mailProps.getProperty("mail.smtp.connectiontimeout"));
        assertEquals("5000", mailProps.getProperty("mail.smtp.writetimeout"));
        assertEquals("true", mailProps.getProperty("mail.debug"));
    }
}