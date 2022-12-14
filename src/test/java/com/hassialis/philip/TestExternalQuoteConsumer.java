package com.hassialis.philip;

import com.hassialis.philip.quotes.external.ExternalQuote;
import com.hassialis.philip.quotes.external.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.awaitility.Awaitility;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class TestExternalQuoteConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(TestExternalQuoteConsumer.class);
	private static final String PROPERTY_NAME = "TestExternalQuoteConsumer";
	@Rule
	public static KafkaContainer kafka = new KafkaContainer();

	private static ApplicationContext context;
	private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

	@BeforeAll
	static void start() {
		kafka.start();
		LOG.debug("Kafka started at {}", kafka.getBootstrapServers());
		context = ApplicationContext.run(
			CollectionUtils.mapOf(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				PROPERTY_NAME, StringUtils.TRUE
			),
			Environment.TEST);
	}

	@AfterAll
	static void stop() {
		kafka.stop();
	}

	@Test
	void consumingPriceUpdatesWorksCorrectly() {
		final TestScopedExternalQuoteProducer testProducer = context.getBean(TestScopedExternalQuoteProducer.class);
		IntStream.range(0,4).forEach(count -> {
			testProducer.send(new ExternalQuote("TEST"+count, randomValue(RANDOM), randomValue(RANDOM) ));
		});
		var observer = context.getBean(PriceUpdateObserver.class);
		Awaitility.await().untilAsserted(()->{
			assertEquals(4, observer.inspected.size());
		});

	}

	private BigDecimal randomValue(final ThreadLocalRandom random) {
		return new BigDecimal(random.nextDouble(0, 1000));
	}

	@Singleton
	@Requires(env = Environment.TEST)
	@Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
	static class PriceUpdateObserver {
		List<PriceUpdate> inspected = new ArrayList<>(10);

		@KafkaListener(
			clientId = "price-update-observer",
			offsetReset = OffsetReset.EARLIEST
		)
		@Topic("price_update")
		void receive(List<PriceUpdate> priceUpdates) {
			LOG.debug("Consumed quote {}", priceUpdates);
			inspected.addAll(priceUpdates);
		}
	}

	@KafkaClient(id = "external-quote-producer")
	@Requires(env = Environment.TEST)
	@Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
	public interface TestScopedExternalQuoteProducer {

		@Topic("external-quotes")
		void send(ExternalQuote externalQuote);
	}

}
