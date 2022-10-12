package io.github.tobiasz.domain.service;

import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.Map;

public interface AuthorService {

	Map<FieldDescriptor, Object> getAuthor(Integer authorId);

}
