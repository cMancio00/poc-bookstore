package mancio.bookstore.repository;

import java.util.List;

import mancio.bookstore.model.Publisher;

public interface PublisherRepository {

	List<Publisher> findAll();

	Publisher findById(int id);

	void save(Publisher publisher);

	void delete(int id);

}
