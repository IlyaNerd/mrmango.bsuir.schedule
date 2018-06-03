package mrmango.bsuir.schedule.main;

import mrmango.bsuir.schedule.config.AppConfig;
import mrmango.bsuir.schedule.services.ScheduleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScheduleService appService = context.getBean(ScheduleService.class);
        appService.checkSchedule();
    }
}
