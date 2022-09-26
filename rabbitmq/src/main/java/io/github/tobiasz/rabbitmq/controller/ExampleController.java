package io.github.tobiasz.rabbitmq.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/something")
@RequiredArgsConstructor
public class ExampleController {

	private final RabbitTemplate rabbitTemplate;

	@GetMapping("/create-something")
	public void createSomething() {
		String eventParam = "created something";
		rabbitTemplate.convertAndSend("some-exchange", "routing-key", eventParam);
	}

}
