package mancio.bookstore.controller;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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

	@Nested
	@DisplayName("Happy cases")
	class HappyCases {
		@Test
		@DisplayName("Find all publishers")
		void testFindAllPublishers() {
			List<Publisher> publishers = asList(new Publisher());
			when(publisherRepository.findAll()).thenReturn(publishers);
			bookStoreController.findAllPublishers();
			verify(publisherView).showAllPublishers(publishers);
		}

		@Test
		@DisplayName("New Publisher when is not already existing")
		void testNewPublisherWhenNotAlreadyExisting() {
			Publisher publisher = new Publisher("test");
			bookStoreController.addNewPublisher(publisher);
			InOrder inOrder = inOrder(publisherRepository, publisherView);
			inOrder.verify(publisherRepository).save(publisher);
			inOrder.verify(publisherView).publisherAdded(publisher);
		}

		@Test
		@DisplayName("Delete Publisher when is present")
		void testDeletePublisherWhenExists() {
			Publisher publisher = new Publisher(1,"test");
			when(publisherRepository.findById(1)).thenReturn(publisher);
			bookStoreController.deletePublisher(publisher);
			InOrder inOrder = inOrder(publisherRepository, publisherView);
			inOrder.verify(publisherRepository).delete(1);
			inOrder.verify(publisherView).publisherRemoved(publisher);
			verifyNoMoreInteractions(ignoreStubs(publisherRepository));
		}
	}

	@Nested
	@DisplayName("Error Cases")
	class ErrorCases {
		@Test
		@DisplayName("New Publisher when is already present")
		void testNewPublisherWhenExisting() {
			Publisher existingPublisher = new Publisher(1,"existing");
			Publisher toAdd = new Publisher(1,"toAdd");
			when(publisherRepository.findById(1)).thenReturn(existingPublisher);
			bookStoreController.addNewPublisher(toAdd);
			verify(publisherView).showError("Already existing publisher with id 1", existingPublisher);
			verifyNoMoreInteractions(ignoreStubs(publisherRepository));
		}

		@Test
		@DisplayName("Delete publisher when is not present")
		void testDeletePublisherWhenIsNotPresent() {
			Publisher publisher = new Publisher(1,"test");
			when(publisherRepository.findById(1)).thenReturn(null);
			bookStoreController.deletePublisher(publisher);
			verify(publisherView).showError("No publisher with id 1", publisher);
			verifyNoMoreInteractions(ignoreStubs(publisherRepository));
		}

	}
}
