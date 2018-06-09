package mrmango.bsuir.schedule.services;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class ScheduleService {
    private final HtmlParser htmlParser;
    private final Downloader downloader;
    private final Unarchiver unarchiver;
    private final EmailService emailService;
    private final PdfParser pdfParser;
    private final MarkedFileService markedFileService;
    private final Calendar googleCalendarService;
    private final EventService eventService;

    private LocalDate lastDate = LocalDate.MIN;

    private final List<String> emailsTo;

    @Autowired
    public ScheduleService(HtmlParser htmlParser,
                           Downloader downloader,
                           Unarchiver unarchiver,
                           EmailService emailService,
                           @Value("#{'${emails}'.split(',')}") List<String> emailsTo,
                           PdfParser pdfParser,
                           MarkedFileService markedFileService,
                           Calendar googleCalendarService, EventService eventService) {
        this.htmlParser = htmlParser;
        this.downloader = downloader;
        this.unarchiver = unarchiver;
        this.emailService = emailService;
        this.emailsTo = emailsTo;
        this.pdfParser = pdfParser;
        this.markedFileService = markedFileService;
        this.googleCalendarService = googleCalendarService;
        this.eventService = eventService;
    }

    @Async
    @Scheduled(cron = "0 0 10,17 ? * MON-FRI")
    public void checkSiteSchedule() {
        log.debug("Checking schedule on site for changes");
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
            markedFileService.moveMarkedFiles(markedPdfFiles);
        }
    }
}
