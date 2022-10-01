package io.github.tobiasz.kafka.controller;

import io.github.tobiasz.kafka.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

	// There should prob be a service wrapping these kafka template calls
	private final KafkaTemplate<String, MessageDto> kafkaTemplate;

	@PostMapping
	public void messageRequest(@RequestBody MessageDto messageDto) {
		this.kafkaTemplate.send("something", messageDto);
	}

}
