package mrmango.bsuir.schedule.services

import com.google.api.services.calendar.model.Event
import spock.lang.Specification


/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-17
 * */
class PdfParserSpec extends Specification {

    def "parse pdf with annotations"() {
        given:
        TextToEventParser textParser = Mock(TextToEventParser.class)
        def pdfParser = new PdfParser(textParser)

        when:
        pdfParser.parsePdf(new File(getClass().getClassLoader().getResource("pdf_with_ann.pdf")?.toURI()))

        then:
        1 * textParser.parseTextToEvents([' 11.06. по 23.06.2018', '11\r\n18:55 20:25 ТСИС лк8 803-7', '12\r\n18:55 20:25 ТСИС Пз3 803-7'])
    }

    def "parse pdf get events"() {
        given:
        TextToEventParser textParser = Stub(TextToEventParser.class)
        def events = [new Event().setSummary("sum")]
        textParser.parseTextToEvents(*_) >> events
        def pdfParser = new PdfParser(textParser)

        expect:
        pdfParser.parsePdf(new File(getClass().getClassLoader().getResource("pdf_with_ann.pdf")?.toURI())) == events
    }

    def "parse pdf no annotations"() {
        given:
        TextToEventParser textParser = Mock(TextToEventParser.class)
        def pdfParser = new PdfParser(textParser)

        when:
        pdfParser.parsePdf(new File(getClass().getClassLoader().getResource("pdf_no_ann.pdf")?.toURI()))

        then:
        1 * textParser.parseTextToEvents([])
    }

    def "pdf contains annotations"() {
        expect:
        new PdfParser(new TextToEventParser()).pdfContainsAnnotations(
                new File(getClass().getClassLoader().getResource("pdf_with_ann.pdf")?.toURI()))
    }

    def "pdf contains annotations, no annotations"() {
        expect:
        !new PdfParser(new TextToEventParser()).pdfContainsAnnotations(
                new File(getClass().getClassLoader().getResource("pdf_no_ann.pdf")?.toURI()))
    }
}
