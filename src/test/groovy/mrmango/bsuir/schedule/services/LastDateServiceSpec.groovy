package mrmango.bsuir.schedule.services

import spock.lang.Specification

import java.time.LocalDate

/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class LastDateServiceSpec extends Specification {
    def lastDateFile

    void cleanup() {
        lastDateFile.delete()
    }

    def "last_date file exists"() {
        given:
        lastDateFile = new File("test_last_date_exists")
        lastDateFile << "2016-06-16"

        expect:
        new LastDateService(lastDateFile.getPath()).getLastDate().toString() == "2016-06-16"
        lastDateFile.getText() == "2016-06-16"
    }

    def "last_date file doesn't exist"() {
        given:
        lastDateFile = new File("test_last_date_removed")
        lastDateFile.delete()

        expect:
        new LastDateService(lastDateFile.getPath()).getLastDate() == LocalDate.MIN
        lastDateFile.getText() == LocalDate.MIN.toString()
    }

    def "update last date"() {
        given:
        lastDateFile = new File("test_last_date_update")
        def service = new LastDateService(lastDateFile.getPath())

        when:
        service.updateLastDate(LocalDate.now())

        then:
        service.getLastDate() == LocalDate.now()
        lastDateFile.getText() == LocalDate.now().toString()
    }

    def "does not read comments"() {
        given:
        lastDateFile = new File("test_last_date_comment")
        lastDateFile << "#comment" << "#2016-06-16"

        expect:
        new LastDateService(lastDateFile.getPath()).getLastDate() == LocalDate.MIN
    }
}
