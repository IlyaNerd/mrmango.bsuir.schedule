package mrmango.bsuir.schedule.services;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventReminder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * created by Ilya Aleksandrovich
 * on 09-Jun-2018
 */
@Service
public class EventService {

    public void addReminders(List<Event> events) {
        events.forEach(event -> {
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(
                            new EventReminder().setMethod("popup").setMinutes(60)));
            event.setReminders(reminders);
        });
    }
}
