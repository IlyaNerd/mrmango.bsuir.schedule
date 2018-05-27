package mrmango.bsuir.schedule.main;

import mrmango.bsuir.schedule.config.AppConfig;
import mrmango.bsuir.schedule.services.Downloader;
import mrmango.bsuir.schedule.services.HtmlParser;
import mrmango.bsuir.schedule.services.Unarchiver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.time.LocalDate;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        HtmlParser htmlParser = context.getBean(HtmlParser.class);
        if (htmlParser.checkSchedule(LocalDate.MIN)) {
            String uri = htmlParser.getScheduleUri();
            Downloader downloader = context.getBean(Downloader.class);
            File file = downloader.download("https://iti.bsuir.by" + uri);
            Unarchiver unarchiver = context.getBean(Unarchiver.class);
            unarchiver.unrar(file);
        }
    }
}
