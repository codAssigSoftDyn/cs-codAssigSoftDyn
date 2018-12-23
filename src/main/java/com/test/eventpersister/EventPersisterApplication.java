package com.test.eventpersister;

import com.test.eventpersister.services.EventPersisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

@SpringBootApplication
public class EventPersisterApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(EventPersisterApplication.class);

    @Autowired
    private EventPersisterService eventPersisterService;
    @Autowired
    @Qualifier("threadPoolExecutor")
    private ExecutorService executorService;

    public static void main(String[] args) {

        SpringApplication.run(EventPersisterApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        if (args.length != 1) {
            LOG.info("Exiting. Usage: exactly one argument should be given, but received: {}", Arrays.toString(args));
            return;
        }
        LocalDateTime start = LocalDateTime.now();
        LOG.info("Application started with the following arguments: {}", Arrays.toString(args));

        Path path = Paths.get(args[0]);
        eventPersisterService.persistEvents(path);
        executorService.shutdownNow();

        LocalDateTime end = LocalDateTime.now();
        LOG.info("Application ended, took: {} seconds", ChronoUnit.SECONDS.between(start, end));
    }
}

