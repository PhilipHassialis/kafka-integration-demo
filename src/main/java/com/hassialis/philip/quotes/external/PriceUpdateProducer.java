package com.hassialis.philip.quotes.external;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.reactivex.rxjava3.core.Flowable;
import org.apache.kafka.clients.producer.RecordMetadata;


import java.util.List;

@KafkaClient(batch = true)
public interface PriceUpdateProducer {
	@Topic("price_update")
	Flowable<RecordMetadata> send(List<PriceUpdate> prices);

}
