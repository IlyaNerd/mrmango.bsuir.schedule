package mrmango.bsuir.schedule.main;

import mrmango.bsuir.schedule.config.AppConfig;
import mrmango.bsuir.schedule.parser.HtmlParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        HtmlParser htmlParser = context.getBean(HtmlParser.class);
        System.out.println(htmlParser.checkSchedule(LocalDate.MIN));
        System.out.println(htmlParser.getScheduleUrl());
    }
}
