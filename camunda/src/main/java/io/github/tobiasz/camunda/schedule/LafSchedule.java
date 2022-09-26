package io.github.tobiasz.camunda.schedule;

import io.camunda.zeebe.client.ZeebeClient;
import io.github.tobiasz.camunda.service.CamundaProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LafSchedule {

	private final CamundaProcessService camundaProcessService;

	// Run every 1 minute during work days
	@Scheduled(cron = "1 * * * * MON-FRI", zone = "Europe/Berlin")
	public void startLafJob() {
		this.camundaProcessService.startProcess("HelpLafayetteEscape");
	}

}
