package mrmango.bsuir.schedule.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * created by Ilya Aleksandrovich
 * on 03-Jun-2018
 */
@Configuration
@PropertySource("file:email.properties")
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender(@Value("${mail.smtp.host}") String smtpHost,
                                         @Value("${mail.smtp.port}") Integer smtpPort,
                                         @Value("${mail.smtp.auth}") boolean smtpAuth,
                                         @Value("${mail.smtp.starttls.enable}") boolean smtpStartTls,
                                         @Value("${mail.transport.protocol}") String transportProtocol,
                                         @Value("${mail.debug}") boolean debug,
                                         @Value("${mail.username}") String userName,
                                         @Value("${mail.password}") String password) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(smtpHost);
        javaMailSender.setPort(smtpPort);

        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);

        Properties props = new Properties();
        props.put("mail.transport.protocol", transportProtocol);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", smtpStartTls);
        props.put("mail.debug", debug);

        javaMailSender.setJavaMailProperties(props);
        return javaMailSender;
    }
}
