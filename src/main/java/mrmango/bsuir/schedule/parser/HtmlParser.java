package mrmango.bsuir.schedule.parser;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * created by Ilya Aleksandrovich
 * on 27-May-2018
 */
@Component
public class HtmlParser {
    private static final Logger log = LogManager.getLogger(HtmlParser.class);

    private Document document;
    private final Connection connection;

    public HtmlParser(@Value("${page.url}") String url) {
        log.info("Establishing connection to " + url);
        connection = Jsoup.connect(url);
        reload();
    }

    @SneakyThrows
    public void reload() {
        log.info("Loading the document");
        document = connection.get();
    }

    public boolean checkSchedule(LocalDate prevDate) {
        log.info("Checking schedule for changes");
        Element row = getScheduleElementByGroup();
        String line = row.child(1).text().replace("изменения от", "").trim();
                LocalDate date = LocalDate.parse(line, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        return date.isAfter(prevDate);
    }

    public String getScheduleUrl() {
        log.info("Getting schedule url");
        Element link = getScheduleElementByGroup().child(2).getElementsByTag("a").get(0);
        return link.attr("href");
    }

    private Element getScheduleElementByGroup() {
        log.info("Getting schedule row element");
        Elements elements = document.getElementsByClass("no-print");
        Elements rows = elements.get(0).getElementsByTag("table").get(0).child(0).children();
        for (Element row : rows) {
            if (row.child(0).text().contains("70325")) {
                return row;
            }
        }
        throw new RuntimeException("no group row was found");
    }
}
