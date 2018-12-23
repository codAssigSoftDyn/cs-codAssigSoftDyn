package com.test.eventpersister.services.writerservice;

import com.test.eventpersister.dto.LogEntry;

public interface PersisterWriter {

    void write(LogEntry logEntry) throws InterruptedException;
}
