package mrmango.bsuir.schedule.config;

import org.springframework.context.annotation.*;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Configuration
@ComponentScan("mrmango.bsuir.schedule")
@PropertySource("classpath:application.properties")
@Import(EmailConfig.class)
public class AppConfig {

}
