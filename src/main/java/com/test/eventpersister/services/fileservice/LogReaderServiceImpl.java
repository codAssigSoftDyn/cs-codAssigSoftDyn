package com.test.eventpersister.services.fileservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.eventpersister.dto.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

@Service
public class LogReaderServiceImpl implements LogReaderService {

    private static final Logger LOG = LoggerFactory.getLogger(LogReaderServiceImpl.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Stream<LogEntry> readFile(Path file, final BlockingQueue<LogEntry> blockingQueue) {
        try {
            return Files.lines(file).map(this::convertToObject)
                        .filter(Optional::isPresent)
                        .map(Optional::get);
        } catch (IOException e) {
            String message = "Problem occurred while processing: " + file;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private Optional<LogEntry> convertToObject(final String jsonString) {
        try {
            return Optional.of(MAPPER.readValue(jsonString, LogEntry.class));
        } catch (IOException e) {
            LOG.error("Cannot parse the following line: " + jsonString, e);
        }
        return Optional.empty();
    }
}
