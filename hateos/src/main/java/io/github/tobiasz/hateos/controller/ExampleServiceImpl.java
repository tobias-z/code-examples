package io.github.tobiasz.hateos.controller;

public class ExampleService implements IExampleService {

	private final ExampleRepository repository;

	public ExampleService(ExampleRepository repository) {
		this.repository = repository;
	}

	public void createThing() {

	}
}
