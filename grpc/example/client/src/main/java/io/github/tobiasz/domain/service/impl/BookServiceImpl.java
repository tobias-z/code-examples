package io.github.tobiasz.domain.service.impl;

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.github.tobiasz.Author;
import io.github.tobiasz.Book;
import io.github.tobiasz.BookAuthorServiceGrpc;
import io.github.tobiasz.domain.service.BookService;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

	@GrpcClient("grpc-service")
	private BookAuthorServiceGrpc.BookAuthorServiceStub asyncStub;

	/**
	 * Because we are doing this just for a rest client we have to wait for all the streaming to be done before we return. All
	 * code inside the StreamObserver will be run on a separate thread which is why we have to use a countDownLatch. One could
	 * imagine us using other things such as event driven design or websockets, so that we could respond instantly when a new book
	 * comes through the stream
	 */
	@Override
	public List<Map<FieldDescriptor, Object>> getBooksByAuthor(Integer authorId) {
		try {
			CountDownLatch countDownLatch = new CountDownLatch(1);
			Author request = Author.newBuilder().setAuthorId(authorId).build();
			List<Map<FieldDescriptor, Object>> bookList = new ArrayList<>();
			this.asyncStub.getBooksByAuthor(request, new StreamObserver<>() {
				@Override
				public void onNext(Book book) {
					bookList.add(book.getAllFields());
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

			countDownLatch.await(1, TimeUnit.MINUTES);
			return bookList;
		} catch (InterruptedException e) {
			throw new RuntimeException("Was unable to complete the streaming of finding all books within a minute.");
		}
	}
}
