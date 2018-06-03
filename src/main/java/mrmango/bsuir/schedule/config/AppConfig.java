package mrmango.bsuir.schedule.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Configuration
@ComponentScan("mrmango.bsuir.schedule")
@PropertySource("classpath:application.properties")
@Import({EmailConfig.class, ScheduleConfig.class})
public class AppConfig {

}
