package mancio.bookstore.repository;

import java.util.List;

import org.hibernate.SessionFactory;

import mancio.bookstore.model.Publisher;

public class PublisherRepositoryHibernate implements PublisherRepository {

	private SessionFactory sessionFactory;

	public PublisherRepositoryHibernate(SessionFactory sessionFactory) {
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
		sessionFactory.inTransaction(session ->
			session.merge(publisher));
	}

	@Override
	public void delete(int id) {
		sessionFactory.inTransaction(session ->
			session.remove(
					session.find(Publisher.class, id)));
	}

}
