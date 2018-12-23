package com.test.eventpersister.repositories;

import com.test.eventpersister.domain.Event;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, EventRepositoryCustom {

    @Transactional
    @Cacheable(value = "eventCache")
    Optional<Event> findByEventId(String eventId);

    @CachePut(value = "eventCache", key = "#result.eventId")
    Event save(Event entity);
}
