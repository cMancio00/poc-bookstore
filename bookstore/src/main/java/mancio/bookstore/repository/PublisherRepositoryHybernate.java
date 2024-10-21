package mancio.bookstore.repository;

import java.util.List;

import org.hibernate.SessionFactory;

import mancio.bookstore.model.Publisher;

public class PublisherRepositoryHybernate implements PublisherRepository {

	private SessionFactory sessionFactory;

	public PublisherRepositoryHybernate(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<Publisher> findAll() {
		return sessionFactory.fromTransaction(session ->
		session.createSelectionQuery("from Publisher", Publisher.class)
		.getResultList());
	}

	@Override
	public Publisher findById(int id) {
		return sessionFactory.fromTransaction(session -> session.find(Publisher.class, id));
	}

	@Override
	public void save(Publisher publisher) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub

	}

}
