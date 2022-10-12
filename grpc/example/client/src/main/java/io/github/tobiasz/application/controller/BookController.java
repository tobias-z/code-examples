package io.github.tobiasz.application.controller;

import com.google.protobuf.Descriptors;
import io.github.tobiasz.domain.service.AuthorService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

	private final AuthorService authorService;

	@GetMapping("/author/{id}")
	public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable("id") Integer authorId) {
		return this.authorService.getAuthor(authorId);
	}

}
