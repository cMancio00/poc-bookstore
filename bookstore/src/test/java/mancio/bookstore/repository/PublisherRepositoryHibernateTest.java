package mancio.bookstore.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import mancio.bookstore.model.Publisher;

class PublisherRepositoryHibernateTest {
	private static final Logger LOGGER = LogManager.getLogger(PublisherRepositoryHibernateTest.class);
	private static final String MYSQL_DATABASE = "bookstore";
	private static final int MYSQL_PORT = 3306;
	private static final  String CONNECTION_URL = String.format("jdbc:mysql://localhost:%d/%s", MYSQL_PORT, MYSQL_DATABASE);
	
	
	private static SessionFactory sessionFactory;
	private PublisherRepositoryHibernate publisherRepository;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("hibernate.connection.url",CONNECTION_URL);
		System.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
		System.setProperty("hibernate.connection.username", "bookstore-manager");
		System.setProperty("hibernate.connection.password", "test");
		LOGGER.info("Connecting to {}", CONNECTION_URL);
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	@AfterAll
	static void tearDownAfterClass() {
		sessionFactory.close();
	}

	@BeforeEach
	void setUp() {
		sessionFactory.getCache().evictAllRegions();
		publisherRepository = new PublisherRepositoryHibernate(sessionFactory);
	}

	@AfterEach
	void tearDown() {
		sessionFactory.getSchemaManager().truncateMappedObjects();
	}

	@Test
	void testFindAllWhenDatabaseIsEmpty() {
		assertThat(publisherRepository.findAll()).isEmpty();
	}
	
	@Test
	void testFindAllWhenDatabaseIsNotEmpty(){
		addTestPublishers(new Publisher("test1"));
		addTestPublishers(new Publisher("test2"));
		assertThat(publisherRepository.findAll())
			.containsExactly(
					new Publisher(1,"test1"),
					new Publisher(2,"test2")
					);
	}
	
	@Test
	void testFindByIdWhenIsNotPresent(){
		assertThat(publisherRepository.findById(1)).isNull();
	}
	
	@Test
	void testFindByIdWhenIsPresent(){
		addTestPublishers(new Publisher("test1"));
		addTestPublishers(new Publisher("test2"));
		assertThat(publisherRepository.findById(2)).isEqualTo(new Publisher(2,"test2"));
	}
	
	@Test
	void testSave(){
		Publisher toAdd = new Publisher("toAdd");
		publisherRepository.save(toAdd);
		assertThat(readAllPublisherFromDatabase())
			.containsExactly(new Publisher(1,"toAdd"));
			
	}
	
	@Test
	void testDelete() {
		addTestPublishers(new Publisher("toDelete"));
		publisherRepository.delete(1);
		assertThat(readAllPublisherFromDatabase()).isEmpty();
	}

	private List<Publisher> readAllPublisherFromDatabase() {
		return sessionFactory.fromTransaction(session ->
		session.createSelectionQuery("from Publisher", Publisher.class)
		.getResultList());
	}
	
	private void addTestPublishers(Publisher toAdd) {
		sessionFactory.inTransaction(session ->
			session.persist(toAdd));
	}
	

}
