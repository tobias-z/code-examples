package io.github.tobiasz.application.grpc;

import io.github.tobiasz.Author;
import io.github.tobiasz.Book;
import io.github.tobiasz.BookAuthorServiceGrpc;
import io.github.tobiasz.domain.service.AuthorService;
import io.github.tobiasz.domain.service.BookService;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class BookAuthorServerService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {

	private final AuthorService authorService;
	private final BookService bookService;

	@Override
	public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
		this.authorService.getAuthors().stream()
			.filter(author -> author.getAuthorId() == request.getAuthorId())
			.findFirst()
			.ifPresentOrElse(
				responseObserver::onNext,
				() -> responseObserver.onError(new RuntimeException("No such Author found"))
			);
		responseObserver.onCompleted();
	}

	@Override
	public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
		for (Book book : this.bookService.getBooks()) {
			if (book.getAuthorId() == request.getAuthorId()) {
				responseObserver.onNext(book);
			}
		}

		responseObserver.onCompleted();
	}
}