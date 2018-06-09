package mrmango.bsuir.schedule.services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by Ilya Aleksandrovich
 * on 09-Jun-2018
 */
@Service
@Log4j2
public class PdfParser {

    @SneakyThrows
    public List<Event> parsePdf(File file) {
        List<PDAnnotation> annotations = getAnnotationsFromPdf(file);
        String date = annotations.get(0).getContents().trim();
        String[] split = date.split(" по ");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        LocalDate date2 = LocalDate.parse(split[1], dateFormatter);
        LocalDate date1 = LocalDate.parse(split[0] + date2.getYear(), dateFormatter);

        return annotations.stream()
                .skip(1)
                .map(an -> {
                    String text = an.getContents().replaceAll("[\r\n\t]", " ");
                    int day = Integer.parseInt(text.substring(0, text.indexOf(" ")).trim());
                    int month = getMonth(day, date1.getMonthValue(), date2.getMonthValue());
                    int year = date2.getYear();

                    LocalDate of = LocalDate.of(year, month, day);
                    return parsePdfTextToEvent(text, of);
                })
                .collect(Collectors.toList());
    }

    public boolean pdfContainsAnnotations(File file) {
        log.info("Checking if pdf contains annotations. file: " + file.getPath());
        return !getAnnotationsFromPdf(file).isEmpty();
    }

    @SneakyThrows
    private List<PDAnnotation> getAnnotationsFromPdf(File file) {
        log.info("Parsing file: " + file.getPath());
        PDPageTree pages = PDDocument.load(file).getPages();
        List<PDAnnotation> annotations = new ArrayList<>();
        for (PDPage page : pages) {
            annotations.addAll(page.getAnnotations()
                    .stream()
                    .filter(ann -> ann != null && ann.getContents() != null)
                    .collect(Collectors.toList()));
        }
        return annotations;
    }

    private Event parsePdfTextToEvent(String text, LocalDate eventDate) {
        log.debug("Parsing text to json [" + text + "]");
        Event event = new Event();

        String[] split = text.split(" ");
        List<String> values = Arrays.stream(split)
                .filter(item -> !item.isEmpty())
                .skip(2)
                .map(String::trim)
                .collect(Collectors.toList());

        String[] timeSpl = values.get(0).split(":");
        LocalTime startTime = LocalTime.of(Integer.parseInt(timeSpl[0]), Integer.parseInt(timeSpl[1]));
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime(new Date(
                eventDate.getYear(),
                eventDate.getMonthValue(),
                eventDate.getDayOfMonth(),
                startTime.getHour(),
                startTime.getMinute())));
        event.setStart(eventDateTime);

        LocalTime endTime = startTime.plusHours(3).plusMinutes(5);
        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(new Date(
                        eventDate.getYear(),
                        eventDate.getMonthValue(),
                        eventDate.getDayOfMonth(),
                        endTime.getHour(),
                        endTime.getMinute())));
        event.setEnd(end);

        event.setSummary(values.get(2) + " " + values.get(3));
        event.setLocation(values.get(4));

        return event;
    }

    private int getMonth(int day, int month1, int month2) {
        if (day > 20) {
            return month1;
        } else {
            return month2;
        }
    }
}
