package mrmango.bsuir.schedule.services;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Ilya Aleksandrovich
 * on 03-Jun-2018
 */
@Service
@Slf4j
public class ScheduleService {
    private final HtmlParser htmlParser;
    private final Downloader downloader;
    private final Unarchiver unarchiver;
    private final EmailService emailService;
    private final PdfParser pdfParser;
    private final MarkedFileService markedFileService;
    private final Calendar googleCalendarService;
    private final EventService eventService;

    private final LastDateService lastDateService;

    private final List<String> emailsTo;
    private final String group;

    @Autowired
    public ScheduleService(HtmlParser htmlParser,
                           @Value("${student.group}") String group,
                           Downloader downloader,
                           Unarchiver unarchiver,
                           EmailService emailService,
                           @Value("#{'${emails}'.split(',')}") List<String> emailsTo,
                           PdfParser pdfParser,
                           MarkedFileService markedFileService,
                           Calendar googleCalendarService,
                           EventService eventService,
                           LastDateService lastDateService) {
        this.htmlParser = htmlParser;
        this.group = group;
        this.downloader = downloader;
        this.unarchiver = unarchiver;
        this.emailService = emailService;
        this.emailsTo = emailsTo;
        this.pdfParser = pdfParser;
        this.markedFileService = markedFileService;
        this.googleCalendarService = googleCalendarService;
        this.eventService = eventService;
        this.lastDateService = lastDateService;
    }

    @Async
    @Scheduled(cron = "0 0 10,17 ? * MON-FRI")
    public void checkSiteSchedule() {
        log.debug("Checking schedule on site for changes");
        if (htmlParser.checkSchedule(group, lastDateService.getLastDate())) {
            log.debug("New schedule was found");
            LocalDate date = LocalDate.now();
            lastDateService.updateLastDate(date);
            String uri = htmlParser.getScheduleUri(group);
            File file = downloader.download("https://iti.bsuir.by" + uri);
            unarchiver.unrar(file);
            emailService.sendEmail(emailsTo,
                    "Found new schedule from date: " + date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")),
                    "");
        }
    }

    @Async
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @SneakyThrows
    public void checkMarkedFileSchedule() {
        log.info("Checking downloaded schedule for changes");
        List<File> markedPdfFiles = markedFileService.getMarkedPdfFiles();
        if (!markedPdfFiles.isEmpty()) {
            log.info("Found some marked pdf files to parse");
            List<Event> events = new ArrayList<>();
            markedPdfFiles.stream().map(pdfParser::parsePdf).forEach(events::addAll);
            eventService.addReminders(events);
            log.info("Sending events to calendar");
            for (Event event : events) {
                googleCalendarService.events().insert("primary", event).execute();
            }
            markedFileService.moveParsedFiles(markedPdfFiles);
        }
    }
}
