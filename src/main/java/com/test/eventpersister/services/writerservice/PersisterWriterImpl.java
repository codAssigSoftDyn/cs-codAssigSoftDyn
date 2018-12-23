package com.test.eventpersister.services.writerservice;

import com.test.eventpersister.domain.Event;
import com.test.eventpersister.dto.LogEntry;
import com.test.eventpersister.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PersisterWriterImpl implements PersisterWriter {

    private static final Logger LOG = LoggerFactory.getLogger(PersisterWriterImpl.class);
    private static final Set<String> CURRENT_EVENTS = new HashSet<>();

    @Value("${event.alert.duration}")
    private long maxDuration;
    private EventRepository eventRepository;

    @Autowired
    public PersisterWriterImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void write(final LogEntry logEntry) throws InterruptedException {
        synchronized (CURRENT_EVENTS) {
            while (CURRENT_EVENTS.contains(logEntry.getId())) {
                CURRENT_EVENTS.wait();
            }
            CURRENT_EVENTS.add(logEntry.getId());
        }
        handleLogEntry(logEntry);

        synchronized (CURRENT_EVENTS) {
            CURRENT_EVENTS.remove(logEntry.getId());
            CURRENT_EVENTS.notifyAll();
        }
    }

    private void handleLogEntry(final LogEntry logEntry) {
        Optional<Event> maybeEvent = eventRepository.findByEventId(logEntry.getId());
        Event event = maybeEvent.orElseGet(() -> mapLogEntryToEvent(logEntry));
        switch (logEntry.getState()) {
            case STARTED:
                event.setEventStart(logEntry.getTimestamp());
                break;
            case FINISHED:
                event.setEventEnd(logEntry.getTimestamp());
                break;
        }

        calculateDurationIfPossible(event);
        eventRepository.save(event);
    }

    private void calculateDurationIfPossible(final Event event) {
        if (event.getEventStart() != null && event.getEventEnd() != null) {
            event.setEventDuration(event.getEventEnd() - event.getEventStart());
            event.setEventStart(null);
            event.setEventEnd(null);
            if (event.getEventDuration() > maxDuration) {
                event.setAlert(Boolean.TRUE);
            }
        }
    }

    private Event mapLogEntryToEvent(final LogEntry logEntry) {
        Event event = new Event();
        event.setEventId(logEntry.getId());

        event.setHost(logEntry.getHost());
        event.setType(logEntry.getType());
        return event;
    }
}
