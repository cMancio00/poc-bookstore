package mancio.bookstore.view;

import java.util.List;

import mancio.bookstore.model.Publisher;

public interface PublisherView {

	void showAllPublishers(List<Publisher> publishers);

	void publisherAdded(Publisher publisher);

	void showError(String string);

	void publisherRemoved(Publisher publisher);

}
