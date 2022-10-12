package io.github.tobiasz.domain.service.impl;

import io.github.tobiasz.Book;
import io.github.tobiasz.domain.service.BookService;
import java.util.List;

public class BookServiceImpl implements BookService {

	@Override
	public List<Book> getBooks() {
		return List.of(
			Book.newBuilder().setBookId(1).setAuthorId(1).setTitle("Oliver Twist").setPrice(123.3f).setPages(100).build(),
			Book.newBuilder().setBookId(2).setAuthorId(1).setTitle("A Christmas Carol").setPrice(223.3f).setPages(150).build(),
			Book.newBuilder().setBookId(3).setAuthorId(2).setTitle("Hamlet").setPrice(723.3f).setPages(250).build(),
			Book.newBuilder().setBookId(4).setAuthorId(3).setTitle("Harry Potter").setPrice(423.3f).setPages(350).build(),
			Book.newBuilder().setBookId(5).setAuthorId(3).setTitle("The Casual Vacancy").setPrice(523.3f).setPages(450).build(),
			Book.newBuilder().setBookId(6).setAuthorId(4).setTitle("Mrs. Dalloway").setPrice(623.3f).setPages(550).build()
		);
	}
}
