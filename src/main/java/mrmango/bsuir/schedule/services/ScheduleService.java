package mrmango.bsuir.schedule.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * created by Ilya Aleksandrovich
 * on 03-Jun-2018
 */
@Service
public class ScheduleService {
    private static final Logger log = LogManager.getLogger(ScheduleService.class);

    private final HtmlParser htmlParser;
    private final Downloader downloader;
    private final Unarchiver unarchiver;
    private final EmailService emailService;

    private LocalDate lastDate = LocalDate.MIN;

    private final List<String> emailsTo;

    @Autowired
    public ScheduleService(HtmlParser htmlParser,
                           Downloader downloader,
                           Unarchiver unarchiver,
                           EmailService emailService,
                           @Value("#{'${emails}'.split(',')}") List<String> emailsTo) {
        this.htmlParser = htmlParser;
        this.downloader = downloader;
        this.unarchiver = unarchiver;
        this.emailService = emailService;
        this.emailsTo = emailsTo;
    }

    @Async
    @Scheduled(cron = "0 0 10,17 ? * MON-FRI")
    public void checkSchedule() {
        log.debug("Checking schedule");
        if (htmlParser.checkSchedule(lastDate)) {
            log.debug("New schedule was found");
            lastDate = LocalDate.now();
            String uri = htmlParser.getScheduleUri();
            File file = downloader.download("https://iti.bsuir.by" + uri);
            unarchiver.unrar(file);
            emailService.sendEmail(emailsTo,
                    "Found new schedule from date: " + lastDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")),
                    "");
        }
    }
}
