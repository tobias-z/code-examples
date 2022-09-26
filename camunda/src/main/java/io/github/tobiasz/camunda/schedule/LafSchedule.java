package io.github.tobiasz.camunda.schedule;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LafSchedule {

	private final ZeebeClient zeebeClient;

	// Run every 1 minute during work days
	@Scheduled(cron = "1 * * * * MON-FRI", zone = "Europe/Berlin")
	public void startLafJob() {
		this.zeebeClient
			.newCreateInstanceCommand()
			.bpmnProcessId("HelpLafayetteEscape")
			.latestVersion()
			.send()
			.join();
	}

}
