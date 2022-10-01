package io.github.tobiasz.kafka.config.kafka;

import io.github.tobiasz.kafka.dto.MessageDto;
import io.github.tobiasz.kafka.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

	private final KafkaUtil kafkaUtil;

	@Bean
	public ProducerFactory<String, MessageDto> producerFactory() {
		return this.kafkaUtil.createClassProducerFactory();
	}

	@Bean
	public KafkaTemplate<String, MessageDto> kafkaTemplate() {
		return new KafkaTemplate<>(this.producerFactory());
	}
}
