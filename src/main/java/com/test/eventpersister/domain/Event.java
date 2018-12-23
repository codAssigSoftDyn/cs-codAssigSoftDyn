package com.test.eventpersister.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "EVENT",
       indexes = { @Index(columnList = "EVENT_ID") })
public class Event implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", unique = true, nullable = false, length = 50)
    private UUID id;

    @Column(name = "EVENT_ID", nullable = false, unique = true, length = 256)
    private String eventId;

    @Column(name = "EVENT_START")
    private Long eventStart;

    @Column(name = "EVENT_END")
    private Long eventEnd;

    @Column(name = "EVENT_DURATION")
    private Long eventDuration;

    @Column(name = "ALERT")
    private Boolean alert;

    @Column(name = "TYPE", length = 256)
    private String type;

    @Column(name = "HOST", length = 256)
    private String host;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public Long getEventStart() {
        return eventStart;
    }

    public void setEventStart(final Long eventStart) {
        this.eventStart = eventStart;
    }

    public Long getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(final Long eventEnd) {
        this.eventEnd = eventEnd;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(final Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(final Boolean alert) {
        this.alert = alert;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }
}
