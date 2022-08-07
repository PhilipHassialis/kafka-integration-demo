package com.hassialis.philip;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class TestContainerSetup {

    private static final Logger LOG = LoggerFactory.getLogger(TestContainerSetup.class);

    @Rule
    public KafkaContainer kafka = new KafkaContainer();

    @Test
    void testItWorks() {
        kafka.start();
        LOG.debug("Kafka started at {}", kafka.getBootstrapServers());
        kafka.stop();
    }
}
