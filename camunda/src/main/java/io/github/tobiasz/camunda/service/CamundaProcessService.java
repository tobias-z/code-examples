package io.github.tobiasz.camunda.service;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CamundaProcessService {

	private final ZeebeClient zeebeClient;

	public void startProcess(String id) {
		this.zeebeClient
			.newCreateInstanceCommand()
			.bpmnProcessId(id)
			.latestVersion()
			.send()
			.join();
	}

}
