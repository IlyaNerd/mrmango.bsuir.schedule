package mrmango.bsuir.schedule.services

import org.jsoup.nodes.Document
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-18
 * */
class HtmlParserSpec extends Specification {

    def "parse schedule uri"() {
        given:
        JsoupConnector jsoupConnector = stubJsoup()

        expect:
        new HtmlParser(jsoupConnector).getScheduleUri("70325") == "/files/schedule/26.06.2018/70325%2C6_v_26.06.rar"
    }

    def "parse schedule uri, invalid group"() {
        given:
        JsoupConnector jsoupConnector = stubJsoup()

        when:
        new HtmlParser(jsoupConnector).getScheduleUri("123456")

        then:
        thrown RuntimeException
    }

    @Unroll
    def "check schedule prev date: #prevDate"() {
        given: "date on site is 26.06.2018"
        JsoupConnector jsoupConnector = stubJsoup()

        expect:
        new HtmlParser(jsoupConnector).checkSchedule("70325", prevDate) == check

        where:
        prevDate                   | check
        LocalDate.of(2018, 06, 16) | true
        LocalDate.of(2018, 07, 1)  | false
    }

    private JsoupConnector stubJsoup() {
        JsoupConnector jsoupConnector = Stub()
        Document stubDoc = Document.createShell("http://google.com")
        stubDoc.body().append(
                new File(getClass().getClassLoader().getResource("schedule_body.html").toURI()).readLines().join("\n"))
        jsoupConnector.loadDocument() >> stubDoc
        return jsoupConnector
    }
}
