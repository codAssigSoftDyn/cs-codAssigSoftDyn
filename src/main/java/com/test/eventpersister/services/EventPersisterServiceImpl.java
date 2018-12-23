package com.test.eventpersister.services;

import com.test.eventpersister.dto.LogEntry;
import com.test.eventpersister.repositories.EventRepository;
import com.test.eventpersister.services.fileservice.LogReaderService;
import com.test.eventpersister.services.fileservice.LogReaderServiceImpl;
import com.test.eventpersister.services.writerservice.PersisterWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

@Service
public class EventPersisterServiceImpl implements EventPersisterService {

    private static final Logger LOG = LoggerFactory.getLogger(LogReaderServiceImpl.class);
    private static final int BLOCKING_QUEUE_SIZE = 1024;
    private static final int BATCH_SIIZE = 4096;

    private LogReaderService logReaderService;
    private PersisterWriter persisterWriter;
    private ExecutorService executorService;
    private EventRepository eventRepository;

    @Autowired
    public EventPersisterServiceImpl(LogReaderService logReaderService, PersisterWriter persisterWriter,
                                     @Qualifier("threadPoolExecutor") ExecutorService executorService,
                                     EventRepository eventRepository) {
        this.logReaderService = logReaderService;
        this.persisterWriter = persisterWriter;
        this.executorService = executorService;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void persistEvents(Path file) {
        LOG.info("Executing EventPersisterService#persistEvents");

        final BlockingQueue<LogEntry> blockingQueue = new LinkedBlockingQueue<>(BLOCKING_QUEUE_SIZE);
        LongAdder longAdder = new LongAdder();

        final CompletableFuture<Void> populateQueueFuture = startPopulatingQueue(file, blockingQueue);
        final CompletableFuture<Void> writeFuture = startWriting(blockingQueue, longAdder);
        writeFuture.join();
        populateQueueFuture.join();

        LOG.info("Done executing EventPersisterService#persistEvents, elements processed: {}", longAdder.sum());
    }

    private CompletableFuture<Void> startPopulatingQueue(final Path file, final BlockingQueue<LogEntry> blockingQueue) {
        final Stream<LogEntry> logEntryStream = logReaderService.readFile(file, blockingQueue);
        return populateQueue(logEntryStream, blockingQueue);
    }

    private CompletableFuture<Void> startWriting(final BlockingQueue<LogEntry> blockingQueue,
                                                 final LongAdder longAdder) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        return CompletableFuture.allOf(
                makeJobs(blockingQueue, numberOfThreads, longAdder).toArray(new CompletableFuture<?>[0]));
    }

    private CompletableFuture<Void> populateQueue(final Stream<LogEntry> logEntryStream,
                                                  final BlockingQueue<LogEntry> blockingQueue) {
        Runnable runnable = () -> {
            logEntryStream.forEach(entry -> putElement(blockingQueue, entry));
            putElement(blockingQueue, new LogEntry());
        };
        return CompletableFuture.runAsync(runnable, executorService);
    }

    private void putElement(final BlockingQueue<LogEntry> blockingQueue,
                            final LogEntry logEntry) {
        try {
            blockingQueue.put(logEntry);
        } catch (InterruptedException e) {
            String message = "Problem occurred while adding end element to the queue";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private List<CompletableFuture<Void>> makeJobs(final BlockingQueue<LogEntry> blockingQueue,
                                                   int numberOfThreads,
                                                   final LongAdder longAdder) {
        AtomicLong persistCounter = new AtomicLong(0);
        List<CompletableFuture<Void>> jobs = new ArrayList<>(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            jobs.add(CompletableFuture.runAsync(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        LogEntry logEntry = blockingQueue.take();
                        if (logEntry.isEmpty()) {
                            blockingQueue.put(logEntry);
                            break;
                        }
                        persisterWriter.write(logEntry);
                        longAdder.increment();
                        final long currentCount = persistCounter.getAndIncrement();
                        if (currentCount % BATCH_SIIZE == 0) {
                            eventRepository.flushAndClear();
                        }
                    }
                } catch (InterruptedException e) {
                    String message = "Problem occurred";
                    LOG.error(message, e);
                    throw new RuntimeException(message, e);
                }
            }, executorService));
        }
        return jobs;
    }
}
