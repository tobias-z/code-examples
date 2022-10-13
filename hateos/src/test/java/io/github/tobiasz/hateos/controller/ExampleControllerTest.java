package io.github.tobiasz.hateos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExampleControllerTest {

	private ExampleController exampleController;

	@BeforeEach
	void setUp() {
		ExampleRepository exampleRepository = new ExampleRepositoryImpl();
		MockExampleRepository mockExampleRepository = new MockExampleRepository();
		this.exampleController = new ExampleController(exampleRepository, new ExampleServiceImpl(mockExampleRepository));
	}

	@Test
	@DisplayName("can do thing")
	void canDoThing() throws Exception {
		this.exampleController.doSomething();
		// assert something
	}

	static class MockExampleRepository implements ExampleRepository {

		@Override
		public void doDatabase() {
		}
	}
}