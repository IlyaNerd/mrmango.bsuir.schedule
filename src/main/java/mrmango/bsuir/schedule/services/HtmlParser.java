package mrmango.bsuir.schedule.services;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Service
public class HtmlParser {
    private static final Logger log = LogManager.getLogger(HtmlParser.class);

    private Document document;
    private final Connection connection;
    private final String group;

    public HtmlParser(@Value("${page.url}") String url,
                      @Value("${student.group}") String group) {
        log.info("Establishing connection to " + url);
        connection = Jsoup.connect(url);
        this.group = group;
        reload();
    }

    @SneakyThrows
    public void reload() {
        log.info("Loading the document");
        document = connection.get();
    }

    public boolean checkSchedule(LocalDate prevDate) {
        log.info("Checking schedule for changes");
        Element row = getScheduleRowByGroup(group);
        String rawDate = row.child(1).text().replaceAll("[^\\d.]", "");
        log.info("Current schedule date is " + rawDate);
        LocalDate date = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        return date.isAfter(prevDate);
    }

    public String getScheduleUri() {
        log.info("Getting schedule url");
        Element link = getScheduleRowByGroup(group).child(2).getElementsByTag("a").get(0);
        String uri = link.attr("href");
        log.info("Schedule uri is " + uri);
        return uri;
    }

    private Element getScheduleRowByGroup(String groupNum) {
        log.debug("Getting schedule row element by group " + groupNum);
        Elements elements = document.getElementsByClass("no-print");
        Elements rows = elements.get(0).getElementsByTag("table").get(0).child(0).children();
        for (Element row : rows) {
            if (row.child(0).text().contains(groupNum)) {
                return row;
            }
        }
        throw new RuntimeException("no group row was found");
    }
}
