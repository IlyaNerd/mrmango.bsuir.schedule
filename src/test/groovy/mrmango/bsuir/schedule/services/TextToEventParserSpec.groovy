package mrmango.bsuir.schedule.services

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class TextToEventParserSpec extends Specification {
    TextToEventParser textParser = new TextToEventParser()

    @Unroll
    def "parse dates from to. dateStr: #dateStr"() {
        expect:
        def dates = textParser.parseDatesFromTo(dateStr)
        dates[0] == date1
        dates[1] == date2

        where:
        dateStr                    | date1                      | date2
        "с 11.06. по 23.06.2018"   | LocalDate.of(2018, 06, 11) | LocalDate.of(2018, 06, 23)
        "11.06. по 23.06.2018"     | LocalDate.of(2018, 06, 11) | LocalDate.of(2018, 06, 23)
        "11.06 по 23.06.2018"      | LocalDate.of(2018, 06, 11) | LocalDate.of(2018, 06, 23)
        "11.06.2018 по 23.06.2018" | LocalDate.of(2018, 06, 11) | LocalDate.of(2018, 06, 23)
        "25.12. по 08.01.2019"     | LocalDate.of(2018, 12, 25) | LocalDate.of(2019, 01, 8)
        "25.12.2018 по 08.01.2019" | LocalDate.of(2018, 12, 25) | LocalDate.of(2019, 01, 8)
    }

    @Unroll
    def "parse pdf text to event. pdfText: #pdfText"() {
        expect:
        def event = textParser.parsePdfTextToEvent(pdfText,
                LocalDate.of(2018, 05, 23),
                LocalDate.of(2018, 06, 11))
        event.getStart().getDateTime().toString() == start
        event.getEnd().getDateTime().toString() == end
        event.getSummary() == summary
        event.getLocation() == location

        where:
        pdfText                                   | start                           | end                             | summary             | location
        "23\n18:55 20:25 ТСИС лк8 803-7"          | "2018-05-23T18:55:00.000+03:00" | "2018-05-23T22:00:00.000+03:00" | "ТСИС лк8"          | "803-7"
        "09\n18:55 20:25 ТСИС лк8 803-7"          | "2018-06-09T18:55:00.000+03:00" | "2018-06-09T22:00:00.000+03:00" | "ТСИС лк8"          | "803-7"
        "09 18:55 20:25 ТСИС лк8 803-7"           | "2018-06-09T18:55:00.000+03:00" | "2018-06-09T22:00:00.000+03:00" | "ТСИС лк8"          | "803-7"
        "23\n18:55 20:25 ОАПЯВУ конс.к экз 602-7" | "2018-05-23T18:55:00.000+03:00" | "2018-05-23T22:00:00.000+03:00" | "ОАПЯВУ конс.к экз" | "602-7"
    }
}
