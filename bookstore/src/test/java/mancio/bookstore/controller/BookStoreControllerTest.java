package mancio.bookstore.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mancio.bookstore.model.Publisher;
import mancio.bookstore.repository.PublisherRepository;
import mancio.bookstore.view.PublisherView;

@DisplayName("Test for BookStoreController")
@ExtendWith(MockitoExtension.class)
class BookStoreControllerTest {
	
	@Mock
	private PublisherRepository publisherRepository;
	
	@Mock
	private PublisherView publisherView;
	
	@InjectMocks
	private BookStoreController bookStoreController;
	
	@Test
	@DisplayName("Find all publishers")
	void testFindAllPublishers() {
		List<Publisher> publishers = asList(new Publisher());
		when(publisherRepository.findAll()).thenReturn(publishers);
		bookStoreController.findAllPublishers();
		verify(publisherView).showAllPublishers(publishers);
	}
}
