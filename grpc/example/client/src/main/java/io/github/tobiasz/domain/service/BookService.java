package io.github.tobiasz.domain.service;

import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.List;
import java.util.Map;

public interface BookService {

	List<Map<FieldDescriptor, Object>> getBooksByAuthor(Integer authorName);

}
