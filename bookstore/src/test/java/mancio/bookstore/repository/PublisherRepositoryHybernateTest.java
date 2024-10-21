package mancio.bookstore.repository;

import static org.assertj.core.api.Assertions.*;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mancio.bookstore.model.Publisher;

class PublisherRepositoryHybernateTest {

	private static SessionFactory sessionFactory;
	private PublisherRepositoryHybernate publisherRepository;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	@AfterAll
	static void tearDownAfterClass() {
		sessionFactory.close();
	}

	@BeforeEach
	void setUp() {
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

	private void addTestPublishers(Publisher toAdd) {
		sessionFactory.inTransaction(session ->
			session.persist(toAdd));
	}

}
