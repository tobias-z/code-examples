package io.github.tobiasz.domain.service.impl;

import static io.github.tobiasz.util.ThreadUtil.waitForMinutes;

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.github.tobiasz.Author;
import io.github.tobiasz.Book;
import io.github.tobiasz.BookAuthorServiceGrpc;
import io.github.tobiasz.domain.service.AuthorService;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

	@GrpcClient("grpc-service")
	private BookAuthorServiceGrpc.BookAuthorServiceBlockingStub blockingStub;

	@GrpcClient("grpc-service")
	private BookAuthorServiceGrpc.BookAuthorServiceStub asyncStub;

	@Override
	public Map<FieldDescriptor, Object> getAuthor(Integer authorId) {
		Author request = Author.newBuilder().setAuthorId(authorId).build();
		Author author = this.blockingStub.getAuthor(request);
		return author.getAllFields();
	}

	public Map<String, Map<FieldDescriptor, Object>> getAuthorOfCheapestBook(List<Integer> bookIdList) {
		return waitForMinutes(1, (countDownLatch) -> {
			Map<String, Map<FieldDescriptor, Object>> result = new HashMap<>();
			StreamObserver<Book> authorOfCheapestBook = this.asyncStub.getAuthorOfCheapestBook(new StreamObserver<>() {

				@Override
				public void onNext(Author author) {
					result.put("cheapestBook", author.getAllFields());
				}

				@Override
				public void onError(Throwable throwable) {
					System.out.println("got an error: " + throwable.getMessage());
					countDownLatch.countDown();
				}

				@Override
				public void onCompleted() {
					System.out.println("completed the stream");
					countDownLatch.countDown();
				}
			});

			bookIdList.stream()
				.map(id -> Book.newBuilder().setBookId(id).build())
				.forEach(authorOfCheapestBook::onNext);

			authorOfCheapestBook.onCompleted();
			return result;
		});
	}

}
