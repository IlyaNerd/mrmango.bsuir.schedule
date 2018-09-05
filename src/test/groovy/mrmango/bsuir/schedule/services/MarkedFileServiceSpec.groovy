package mrmango.bsuir.schedule.services

import spock.lang.Shared
import spock.lang.Specification


/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-17
 * */
class MarkedFileServiceSpec extends Specification {
    @Shared
    File testDir = new File(System.getProperty("java.io.tmpdir") + "/test_dir")

    void setup() {
        testDir.mkdir()
    }

    void cleanup() {
        testDir.eachFileRecurse { it.delete() }
    }

    void cleanupSpec() {
        testDir.deleteDir()
    }

    def "get marked files in dir"() {
        given:
        new File(testDir, "1.pdf") << "first"
        new File(testDir, "2.txt") << "second"

        and:
        PdfParser pdfParser = Stub(PdfParser)
        pdfParser.pdfContainsAnnotations(*_) >> true

        expect:
        new MarkedFileService(pdfParser, testDir.getPath()).getMarkedPdfFiles() == [new File(testDir, "1.pdf")]
    }

    def "no marked files in dir"() {
        given:
        new File(testDir, "1.txt") << "not pdf"
        PdfParser pdfParser = Mock(PdfParser)
        0 * pdfParser.pdfContainsAnnotations(*_)

        expect:
        new MarkedFileService(pdfParser, testDir.getPath()).getMarkedPdfFiles() == []
    }

    def "move parsed files, parsed dir exists"() {
        given:
        def file = new File(testDir, "1.pdf") << "pdf"
        new File(testDir, "parsed").mkdirs()

        when:
        new MarkedFileService(Stub(PdfParser), testDir.getPath()).moveParsedFiles([file])

        then:
        new File(testDir, "parsed/1.pdf").exists()
    }

    def "move parsed files, parsed dir doesn't exist"() {
        given:
        def file = new File(testDir, "1.pdf") << "pdf"

        when:
        new MarkedFileService(Stub(PdfParser), testDir.getPath()).moveParsedFiles([file])

        then:
        new File(testDir, "parsed/1.pdf").exists()
    }

}
