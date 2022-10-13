package io.github.tobiasz.hateos.controller;

public class ExampleController {

	private final ExampleRepository exampleRepository;
	private final ExampleService exampleService;

	public ExampleController(ExampleRepository exampleRepository, ExampleService exampleService) {
		this.exampleRepository = exampleRepository;
		this.exampleService = exampleService;
	}

	public void doSomething() {
		this.exampleRepository.doDatabase();
		this.exampleService.createThing();
	}

}
