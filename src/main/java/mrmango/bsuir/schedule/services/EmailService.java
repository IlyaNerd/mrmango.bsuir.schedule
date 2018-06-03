package mrmango.bsuir.schedule.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by Ilya Aleksandrovich
 * on 03-Jun-2018
 */
@Service
public class EmailService {
    private static final Logger log = LogManager.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(List<String> to, String subject, String text) {
        log.info("Sending email to " + to.toString());
        log.info("With subject [" + subject + "]");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("check.schedule.spring");
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info(String.format("Email to %s was sent!", to.toString()));
    }
}
