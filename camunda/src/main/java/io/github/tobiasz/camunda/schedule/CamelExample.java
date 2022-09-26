package io.github.tobiasz.camunda.schedule;

import io.github.tobiasz.camunda.service.CamundaProcessService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CamelExample extends RouteBuilder {

	private final CamundaProcessService camundaProcessService;

	@Override
	public void configure() throws Exception {
		this.from("file:camel/output")
			.routeId("camunda-with-camel")
			.process(exchange -> this.camundaProcessService.startProcess("HelpLafayetteEscape"));
	}
}
