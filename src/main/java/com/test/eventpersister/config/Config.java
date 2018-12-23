package com.test.eventpersister.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan
@EnableCaching
public class Config {

    @Bean(name = "threadPoolExecutor")
    public ExecutorService getAsyncExecutor() {
        return Executors.newCachedThreadPool();
    }
}
