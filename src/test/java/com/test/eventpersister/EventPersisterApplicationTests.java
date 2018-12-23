package com.test.eventpersister;

import com.test.eventpersister.domain.Event;
import com.test.eventpersister.repositories.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventPersisterApplicationTests {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private EventRepository eventRepository;


    @Test
    public void testApp() throws Exception {
        File file = ResourceUtils.getFile("classpath:events.json");

        CommandLineRunner runner = ctx.getBean(CommandLineRunner.class);
        runner.run(file.getAbsolutePath());

        final List<Event> allEvents = eventRepository.findAll();
        assertEquals(2, allEvents.size());
        final Map<String, Event> events =
                allEvents.stream().collect(Collectors.toMap(Event::getEventId, Function.identity()));

        assertTrue(events.containsKey("eventid0"));
        assertTrue(events.containsKey("eventid1"));

        Event event = events.get("eventid0");
        assertNotNull(event.getId());
        assertNull(event.getEventStart());
        assertNull(event.getEventEnd());
        assertEquals("eventid0", event.getEventId());
        assertEquals(Long.valueOf(2), event.getEventDuration());
        assertNull(event.getAlert());

        event = events.get("eventid1");
        assertNotNull(event.getId());
        assertNull(event.getEventStart());
        assertNull(event.getEventEnd());
        assertEquals("eventid1", event.getEventId());
        assertEquals(Long.valueOf(7), event.getEventDuration());
        assertTrue(event.getAlert());
    }
}

