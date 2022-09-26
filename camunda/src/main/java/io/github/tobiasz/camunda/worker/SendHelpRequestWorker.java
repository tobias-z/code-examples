package io.github.tobiasz.camunda.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.stereotype.Component;

@Component
public class SendHelpRequestWorker {

	@ZeebeWorker(type = "sendLetter", autoComplete = true)
	public void sendLetter(ActivatedJob job) {
		System.out.println("doing job");
		System.out.println(job);
	}

}
