package mrmango.bsuir.schedule.services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification


/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class EmailServiceSpec extends Specification {

    def "send email"() {
        given:
        def mailMock = Mock(JavaMailSender.class)
        def testMassage = new SimpleMailMessage()
        testMassage.to = ["test_me@mail.com"]
        testMassage.from = "check.schedule.spring"
        testMassage.subject = "test_subject"
        testMassage.text = "test_text"

        and:
        EmailService emailService = new EmailService(mailMock)

        when:
        emailService.sendEmail(["test_me@mail.com"], "test_subject", "test_text")

        then:
        1 * mailMock.send(testMassage)
    }
}