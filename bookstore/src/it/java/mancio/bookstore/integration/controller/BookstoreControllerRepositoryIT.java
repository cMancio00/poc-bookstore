package mancio.bookstore.integration.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import mancio.bookstore.controller.BookStoreController;
import mancio.bookstore.model.Publisher;
import mancio.bookstore.repository.PublisherRepository;
import mancio.bookstore.repository.PublisherRepositoryHibernate;
import mancio.bookstore.view.PublisherView;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookstoreControllerRepositoryIT {

	@SuppressWarnings({ "rawtypes", "resource" })
	@Container
	public static final MySQLContainer mysql = new MySQLContainer("mysql:8.0.28")
			.withDatabaseName("test-db-it")
			.withUsername("bookstore-manager")
			.withPassword("test");

	private static SessionFactory sessionFactory;

	private PublisherRepository publisherRepository;

	@Mock
	private PublisherView publisherView;

	@InjectMocks
	private BookStoreController bookStoreController;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Properties mysqlProperties = new Properties();
		mysqlProperties.setProperty("hibernate.connection.driver_class","com.mysql.cj.jdbc.Driver");
		mysqlProperties.setProperty("hibernate.connection.url", mysql.getJdbcUrl());
		mysqlProperties.setProperty("hibernate.connection.username", mysql.getUsername());
		mysqlProperties.setProperty("hibernate.connection.password", mysql.getPassword());
		System.setProperty("hibernate.connection.url", mysql.getJdbcUrl());
		System.setProperty("hibernate.connection.username", mysql.getUsername());
		System.setProperty("hibernate.connection.password", mysql.getPassword());
		sessionFactory = new Configuration().setProperties(mysqlProperties).configure("hibernate-integration.cfg.xml").buildSessionFactory();
	}

	@BeforeEach
	void setUp() {
		publisherRepository = new PublisherRepositoryHibernate(sessionFactory);
		sessionFactory.getCache().evictAllRegions();
		bookStoreController = new BookStoreController(publisherRepository, publisherView);
	}

	@AfterEach
	void tearDown() {
		sessionFactory.getSchemaManager().truncateMappedObjects();
	}
	
	
	@Test
	@DisplayName("Find all publishers")
	void testFindAllPublishers() {
		publisherRepository.save(new Publisher("saved"));
		bookStoreController.findAllPublishers();
		verify(publisherView).showAllPublishers(asList(new Publisher(1, "saved")));
	}

	@Test
	@DisplayName("New Publisher when is not already existing")
	void testNewPublisherWhenNotAlreadyExisting() {
		bookStoreController.addNewPublisher(new Publisher("newPublisher"));
		verify(publisherView).publisherAdded(new Publisher(0, "newPublisher"));
	}

	@Test
	@DisplayName("Delete Publisher when is existing")
	void testDeletePublisher() {
		var publisher = new Publisher("toDelete");
		publisherRepository.save(publisher);
		bookStoreController.deletePublisher(1);
		verify(publisherView).publisherRemoved(new Publisher(1, "toDelete"));
	}

}
