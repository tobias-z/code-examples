package io.github.tobiasz.hateos.controller;

public class ExampleServiceImpl implements ExampleService {

	private final ExampleRepository repository;

	public ExampleServiceImpl(ExampleRepository repository) {
		this.repository = repository;
	}

	public void createThing() {

	}
}
