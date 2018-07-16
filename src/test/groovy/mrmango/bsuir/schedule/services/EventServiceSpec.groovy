package mrmango.bsuir.schedule.services

import com.google.api.services.calendar.model.Event
import spock.lang.Specification


/**
 *
 * @author Ilya Aleksandrovich
 * created on 2018-07-16
 * */
class EventServiceSpec extends Specification {
    def "add reminders"() {
        given:
        def events = [new Event(), new Event()]

        when:
        new EventService().addReminders(events)

        then: "Events should have popups with 60 mins"
        events.each {
            it.getReminders().getOverrides().get(0).getMethod() == "popup"
            it.getReminders().getOverrides().get(0).getMinutes() == 60
        }
    }
}
