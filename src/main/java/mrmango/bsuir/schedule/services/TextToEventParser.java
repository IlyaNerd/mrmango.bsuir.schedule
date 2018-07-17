package mrmango.bsuir.schedule.services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 **/
@Service
@Log4j2
public class TextToEventParser {

    public List<Event> parseTextToEvents(List<String> text) {
        if(text.isEmpty()) {
            throw new IllegalArgumentException("Text to parse is empty");
        }

        LocalDate[] dates = parseDatesFromTo(text.get(0).trim());
        LocalDate from = dates[0];
        LocalDate to = dates[1];

        return text.stream()
                .skip(1) //date from to
                .map(txt -> parseTextToEvent(txt, from, to))
                .collect(Collectors.toList());
    }

    /**
     * parses event date from and event date to from str like: "с 11.06. по 23.06.2018"
     *
     * @param pdfText text to parse
     * @return array of 2 dates, [0] = from, [1] = to
     */
    public LocalDate[] parseDatesFromTo(String pdfText) {
        Pattern compile = Pattern.compile("\\d+\\.\\d+\\.*\\d*");
        Matcher matcher = compile.matcher(pdfText);
        if (!matcher.find()) {
            throw new IllegalArgumentException("first match was not found");
        }
        String dateStr1 = matcher.group();

        if (!matcher.find()) {
            throw new IllegalArgumentException("second match was not found");
        }
        String dateStr2 = matcher.group();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        LocalDate date2 = LocalDate.parse(dateStr2, dateFormatter);

        if (!Pattern.compile("\\d+\\.\\d+\\.\\d+").matcher(dateStr1).matches()) {
            if (!dateStr1.endsWith(".")) {
                dateStr1 += ".";
            }
            dateStr1 += date2.getYear();
        }

        LocalDate date1 = LocalDate.parse(dateStr1, dateFormatter);

        if (date1.getMonthValue() == 12 && date1.getDayOfMonth() > 20 && date1.getYear() != date2.getYear() - 1) {
            // in case lessons are in the end of the year
            date1 = date1.minusYears(1);
        }

        return new LocalDate[]{date1, date2};
    }

    /**
     * parses text like: "23 18:55 20:25 ТСИС лк8 803-7"
     * to even like: {end={dateTime=2018-05-23T22:00:00.000+03:00}, location=803-7, start={dateTime=2018-05-23T18:55:00.000+03:00}, summary=ТСИС лк8}
     *
     * @param pdfText text to parse
     * @param from event date from
     * @param to event date to
     * @return event with start, end dates, location and summary
     */
    public Event parseTextToEvent(String pdfText, LocalDate from, LocalDate to) {
        log.debug("Parsing text to event [" + pdfText + "]");
        String text = pdfText.replaceAll("[\r\n\t]", " ");
        LocalDate eventDate = parseEventDate(text, from, to);
        Event event = new Event();

        Pattern timePattern = Pattern.compile("\\d{2}:\\d{2}");
        Matcher timeMatcher = timePattern.matcher(text);
        if(!timeMatcher.find()) {
            throw new IllegalArgumentException("Unable to parse event time from text: <" + text + ">");
        }
        String[] timeSpl = timeMatcher.group().split(":");
        LocalTime startTime = LocalTime.of(Integer.parseInt(timeSpl[0]), Integer.parseInt(timeSpl[1]));
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime(LocalDateTime.of(eventDate, startTime)
                .atOffset(ZoneOffset.of("+03:00"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        event.setStart(eventDateTime);

        LocalTime endTime = startTime.plusHours(3).plusMinutes(5);
        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(LocalDateTime.of(eventDate, endTime)
                        .atOffset(ZoneOffset.of("+03:00"))
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        event.setEnd(end);

        Pattern locationPattern = Pattern.compile("\\d{3}-\\d{1,2}");
        Matcher locationMatcher = locationPattern.matcher(text);
        if (!locationMatcher.find()) {
            throw new IllegalArgumentException("Unable to parse location from text: <" + text + ">");
        }
        event.setLocation(locationMatcher.group());

        String summary = Arrays.stream(text.split(" "))
                .filter(item -> !item.isEmpty())
                .filter(item -> !timePattern.asPredicate().test(item))
                .filter(item -> !locationPattern.asPredicate().test(item))
                .skip(1)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        event.setSummary(summary);

        log.debug("Parsed text to event: " + event.toString());
        return event;
    }

    private LocalDate parseEventDate(String text, LocalDate from, LocalDate to) {
        int day = Integer.parseInt(text.substring(0, text.indexOf(" ")).trim());
        int month = getMonth(day, from.getMonthValue(), to.getMonthValue());
        int year = to.getYear();
        return LocalDate.of(year, month, day);
    }

    private int getMonth(int day, int month1, int month2) {
        if (day > 20) {
            return month1;
        } else {
            return month2;
        }
    }

}
