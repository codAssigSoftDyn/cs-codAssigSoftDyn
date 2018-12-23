package com.test.eventpersister.repositories;

import org.springframework.transaction.annotation.Transactional;

public interface EventRepositoryCustom {

    @Transactional
    void flushAndClear();
}
