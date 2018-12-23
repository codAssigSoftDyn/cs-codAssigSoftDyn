package com.test.eventpersister.dto;

public class LogEntry {
    private String id;
    private EventState state;
    private String type;
    private String host;
    private Long timestamp;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public EventState getState() {
        return state;
    }

    public void setState(final EventState state) {
        this.state = state;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEmpty() {
        return id == null &&
               state == null &&
               type == null &&
               host == null &&
               timestamp == null;
    }
}
