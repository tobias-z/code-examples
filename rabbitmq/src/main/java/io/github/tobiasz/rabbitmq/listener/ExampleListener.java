package io.github.tobiasz.rabbitmq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ExampleListener {

	@RabbitListener(queues = {"SomeQueue"})
	public void someQueueListener(Object event) {
		System.out.println(event);
	}
}
