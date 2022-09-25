package io.github.tobiasz.camel.service;

import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Service;

@Service
public class MyService {

	public void validate(GenericFile<String> content) {
		System.out.println("validating content: " + content.getFileName());
	}

	public void process(GenericFile<String> content) {
		System.out.println("processing content: " + content.getFileName());
	}

}
