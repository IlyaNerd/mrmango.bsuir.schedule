package mrmango.bsuir.schedule.config;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Configuration
@ComponentScan("mrmango.bsuir.schedule")
@PropertySource("classpath:application.properties")
@Import(EmailConfig.class)
@EnableScheduling
public class AppConfig {

}
