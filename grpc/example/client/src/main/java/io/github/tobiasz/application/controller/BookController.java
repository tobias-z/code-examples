package io.github.tobiasz.application.controller;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.github.tobiasz.domain.service.AuthorService;
import io.github.tobiasz.domain.service.BookService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

	private final AuthorService authorService;
	private final BookService bookService;

	@GetMapping("/author/{id}")
	public Map<Descriptors.FieldDescriptor, Object> getAuthor(@PathVariable("id") Integer authorId) {
		return this.authorService.getAuthor(authorId);
	}

	@GetMapping("author-books/{id}")
	public List<Map<FieldDescriptor, Object>> getBooksByAuthor(@PathVariable("id") Integer authorId) {
		return this.bookService.getBooksByAuthor(authorId);
	}

	@PostMapping("author-cheapest-book")
	public Map<String, Map<FieldDescriptor, Object>> getAuthorOfCheapestBook(@RequestBody List<Integer> bookIdList) {
		return this.authorService.getAuthorOfCheapestBook(bookIdList);
	}

}
