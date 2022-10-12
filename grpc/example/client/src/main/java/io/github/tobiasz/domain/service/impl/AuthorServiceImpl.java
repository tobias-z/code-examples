package io.github.tobiasz.domain.service.impl;

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.github.tobiasz.Author;
import io.github.tobiasz.BookAuthorServiceGrpc;
import io.github.tobiasz.domain.service.AuthorService;
import java.util.Map;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

	@GrpcClient("grpc-service")
	private BookAuthorServiceGrpc.BookAuthorServiceBlockingStub blockingStub;

	public Map<FieldDescriptor, Object> getAuthor(Integer authorId) {
		Author request = Author.newBuilder().setAuthorId(authorId).build();
		Author author = this.blockingStub.getAuthor(request);
		return author.getAllFields();
	}

}
