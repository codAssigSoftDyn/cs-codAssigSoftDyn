package com.test.eventpersister.services;

import java.nio.file.Path;

public interface EventPersisterService {
    void persistEvents(Path file);
}
