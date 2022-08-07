package com.hassialis.philip;

import com.hassialis.philip.quotes.external.ExternalQuote;
import com.hassialis.philip.quotes.external.ExternalQuoteProducer;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@Requires(notEnv = Environment.TEST)
public class EventScheduler {
	private final ExternalQuoteProducer externalQuoteProducer;
	private static final List<String> SYMBOLS = Arrays.asList("AAPL", "GOOG", "MSFT", "AMZN", "META", "TSLA");
	private static final Logger LOG = LoggerFactory.getLogger(EventScheduler.class);

	public EventScheduler(ExternalQuoteProducer externalQuoteProducer) {
		this.externalQuoteProducer = externalQuoteProducer;
	}

	@Scheduled(fixedDelay = "10s")

	void generate() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		final ExternalQuote quote = new ExternalQuote(
				SYMBOLS.get(random.nextInt(0, SYMBOLS.size() - 1)),
				randomValue(random),
				randomValue(random)
		);
		LOG.debug("Generated quote {}", quote);
		externalQuoteProducer.send(quote.getSymbol(), quote);
	}

	private BigDecimal randomValue(final ThreadLocalRandom random) {
		return new BigDecimal(random.nextDouble(0, 1000));
	}


}
