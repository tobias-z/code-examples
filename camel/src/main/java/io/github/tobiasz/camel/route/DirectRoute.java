package io.github.tobiasz.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DirectRoute extends RouteBuilder {


	@Override
	public void configure() throws Exception {
		from("direct:process")
			.routeId("process-route")
			.log("Processing: ${body}")
			.to("bean:myService?method=process");
	}
}
