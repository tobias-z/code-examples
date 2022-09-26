package io.github.tobiasz.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:camel/output")
			.routeId("file-route")
			.log("Body: ${body}")
			.doTry()
				.to("bean:myService?method=validate")
			.doCatch(RuntimeException.class)
				.log("Error: ${exception.message}")
				.stop()
			.to("direct:process")
			.to("file:camel/output");
	}
}
