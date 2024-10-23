package mancio.bookstore.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import mancio.bookstore.model.Publisher;

class PublisherRepositoryHibernateTest {
	private static final String H2_DATABASE = "bookstore";
	private static final String CONNECTION_URL = String.format("jdbc:h2:mem:%s", H2_DATABASE);

	private static SessionFactory sessionFactory;
	private PublisherRepositoryHibernate publisherRepository;

	@BeforeEach
	void setUp() {
		System.setProperty("hibernate.connection.url", CONNECTION_URL);
		System.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		System.setProperty("hbm2ddl.auto", "create-drop");
		sessionFactory = new Configuration().configure().buildSessionFactory();
		publisherRepository = new PublisherRepositoryHibernate(sessionFactory);
	}

	@AfterEach
	void tearDown() {
		sessionFactory.getSchemaManager().truncateMappedObjects();
		sessionFactory.close();
	}

	@Test
	void testFindAllWhenDatabaseIsEmpty() {
		assertThat(publisherRepository.findAll()).isEmpty();
	}

	@Test
	void testFindAllWhenDatabaseIsNotEmpty() {
		addTestPublishers(new Publisher("test1"));
		addTestPublishers(new Publisher("test2"));
		assertThat(publisherRepository.findAll()).containsExactly(new Publisher(1, "test1"), new Publisher(2, "test2"));
	}

	@Test
	void testFindByIdWhenIsNotPresent() {
		assertThat(publisherRepository.findById(1)).isNull();
	}

	@Test
	void testFindByIdWhenIsPresent() {
		addTestPublishers(new Publisher("test1"));
		addTestPublishers(new Publisher("test2"));
		assertThat(publisherRepository.findById(2)).isEqualTo(new Publisher(2, "test2"));
	}

	@Test
	void testSave() {
		Publisher toAdd = new Publisher("toAdd");
		publisherRepository.save(toAdd);
		assertThat(readAllPublisherFromDatabase()).containsExactly(new Publisher(1, "toAdd"));
	}

	@Test
	void testDelete() {
		addTestPublishers(new Publisher("toDelete"));
		publisherRepository.delete(1);
		assertThat(readAllPublisherFromDatabase()).isEmpty();
	}

	private List<Publisher> readAllPublisherFromDatabase() {
		return sessionFactory.fromTransaction(
				session -> session.createSelectionQuery("from Publisher", Publisher.class).getResultList());
	}

	private void addTestPublishers(Publisher toAdd) {
		sessionFactory.inTransaction(session -> session.persist(toAdd));
	}

}
