package mrmango.bsuir.schedule.main;

import mrmango.bsuir.schedule.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
public class Application {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
