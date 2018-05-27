package mrmango.bsuir.schedule.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

/**
 *
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("mrmango.bsuir.schedule")
class AppConfig {

}
