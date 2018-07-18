package mrmango.bsuir.schedule.services;

import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Service
@Log4j2
public class HtmlParser {
    private final JsoupConnector jsoupConnector;

    @Autowired
    public HtmlParser(JsoupConnector jsoupConnector) {
        this.jsoupConnector = jsoupConnector;
    }

    public boolean checkSchedule(String group, LocalDate prevDate) {
        log.info("Checking schedule for changes");
        Element row = getScheduleRowByGroup(group);
        String rawDate = row.child(1).text().replaceAll("[^\\d.]", "");
        log.info("Current schedule date is " + rawDate);
        LocalDate date = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        return date.isAfter(prevDate);
    }

    public String getScheduleUri(String group) {
        log.info("Getting schedule url");
        Element link = getScheduleRowByGroup(group).child(2).getElementsByTag("a").get(0);
        String uri = link.attr("href");
        log.info("Schedule uri is [" + uri + "]");
        return uri;
    }

    private Element getScheduleRowByGroup(String group) {
        log.debug("Getting schedule row element by group [" + group + "]");
        Elements tables = jsoupConnector.loadDocument().getElementsByTag("table");
        for(Element table : tables) {
            Elements rows = table.getElementsByTag("tr");
            for (Element row : rows) {
                if (row.child(0).text().contains(group)) {
                    return row;
                }
            }
        }
        throw new RuntimeException("no group row was found");
    }
}
