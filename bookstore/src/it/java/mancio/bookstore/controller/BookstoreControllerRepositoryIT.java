package mancio.bookstore.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import mancio.bookstore.model.Publisher;
import mancio.bookstore.repository.PublisherRepository;
import mancio.bookstore.repository.PublisherRepositoryHibernate;
import mancio.bookstore.view.PublisherView;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@DisplayName("Controller-Repository IT")
class BookstoreControllerRepositoryIT {
	private static final Logger LOGGER = LogManager.getLogger(BookstoreControllerRepositoryIT.class);

	@SuppressWarnings({ "rawtypes", "resource" })
	@Container
	public static final MySQLContainer mysql = new MySQLContainer("mysql:8.0.28").withDatabaseName("bookstore-db")
			.withUsername("bookstore-manager").withPassword("test");

	private static SessionFactory sessionFactory;

	private PublisherRepository publisherRepository;

	@Mock
	private PublisherView publisherView;

	private BookStoreController bookStoreController;

	private AutoCloseable closeable;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("hibernate.connection.url", mysql.getJdbcUrl());
		System.setProperty("hibernate.connection.username", mysql.getUsername());
		System.setProperty("hibernate.connection.password", mysql.getPassword());
		System.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
		sessionFactory = new Configuration().configure().buildSessionFactory();

	}

	@BeforeEach
	void setUp() {
		publisherRepository = new PublisherRepositoryHibernate(sessionFactory);
		closeable = MockitoAnnotations.openMocks(this);
		sessionFactory.getCache().evictAllRegions();
		bookStoreController = new BookStoreController(publisherRepository, publisherView);
	}

	@AfterEach
	void tearDown() throws Exception {
		sessionFactory.getSchemaManager().truncateMappedObjects();
		closeable.close();
	}
	
	@Test
	@DisplayName("Find all publishers")
	void testFindAllPublishers() {
		publisherRepository.save(new Publisher("saved"));
		bookStoreController.findAllPublishers();
		verify(publisherView).showAllPublishers(asList(new Publisher(1,"saved")));
	}

	@Test
	@DisplayName("New Publisher when is not already existing")
	void testNewPublisherWhenNotAlreadyExisting() {
		bookStoreController.addNewPublisher(new Publisher("newPublisher"));
		verify(publisherView).publisherAdded(new Publisher(0,"newPublisher"));
	}
	@Test
	@DisplayName("Delete Publisher when is existing")
	void testDeletePublisher() {
		var publisher = new Publisher("toDelete");
		publisherRepository.save(publisher);
		bookStoreController.deletePublisher(1);
		verify(publisherView).publisherRemoved(new Publisher(1,"toDelete"));
	}

}
