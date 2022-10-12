package io.github.tobiasz.domain.service.impl;

import io.github.tobiasz.Author;
import io.github.tobiasz.domain.service.AuthorService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

	@Override
	public List<Author> getAuthors() {
		return List.of(
			Author.newBuilder().setAuthorId(1).setBookId(1).setFirstName("Charles").setLastName("Dickens").setGender("male")
				.build(),
			Author.newBuilder().setAuthorId(2).setFirstName("William").setLastName("Shakespeare").setGender("male").build(),
			Author.newBuilder().setAuthorId(3).setFirstName("JK").setLastName("Rowling").setGender("female").build(),
			Author.newBuilder().setAuthorId(4).setFirstName("Virginia").setLastName("Woolf").setGender("female").build()
		);
	}
}
