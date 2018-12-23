package com.test.eventpersister.services.fileservice;

import com.test.eventpersister.dto.LogEntry;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public interface LogReaderService {

    Stream<LogEntry> readFile(Path file, BlockingQueue<LogEntry> blockingQueue);
}
