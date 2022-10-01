package io.github.tobiasz.kafka.listener;

import io.github.tobiasz.kafka.dto.MessageDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SomethingListener {

	@KafkaListener(
		topics = "something",
		groupId = "groupId",
		containerFactory = "messageFactory"
	)
	void somethingListener(MessageDto data) {
		System.out.printf("received data: %s%n", data.message());
	}

}
