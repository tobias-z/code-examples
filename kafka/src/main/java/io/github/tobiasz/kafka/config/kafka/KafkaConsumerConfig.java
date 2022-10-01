package io.github.tobiasz.kafka.config.kafka;

import io.github.tobiasz.kafka.dto.MessageDto;
import io.github.tobiasz.kafka.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

	private final KafkaUtil kafkaUtil;

	@Bean
	public ConsumerFactory<String, MessageDto> messageConsumerFactory() {
		return this.kafkaUtil.createClassConsumerFactory(MessageDto.class);
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MessageDto>> messageFactory() {
		ConcurrentKafkaListenerContainerFactory<String, MessageDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(this.messageConsumerFactory());
		return factory;
	}

}
