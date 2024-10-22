package mancio.bookstore.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import mancio.bookstore.model.Publisher;

@Testcontainers
class PublisherRepositoryHibernateTest {

	@SuppressWarnings({ "rawtypes", "resource" })
	@Container
	public static final MySQLContainer mysql = 
		new MySQLContainer("mysql:8.0.28")
			.withDatabaseName("bookstore-db")
			.withUsername("bookstore-manager")
			.withPassword("test");

	
	private static SessionFactory sessionFactory;
	private PublisherRepositoryHybernate publisherRepository;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("hibernate.connection.url", mysql.getJdbcUrl());
		System.setProperty("hibernate.connection.username", mysql.getUsername());
		System.setProperty("hibernate.connection.password", mysql.getPassword());
		System.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	@AfterAll
	static void tearDownAfterClass() {
		sessionFactory.close();
	}

	@BeforeEach
	void setUp() {
		sessionFactory.getCache().evictAllRegions();
		publisherRepository = new PublisherRepositoryHybernate(sessionFactory);
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
