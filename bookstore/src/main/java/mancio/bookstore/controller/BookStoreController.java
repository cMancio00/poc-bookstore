package mancio.bookstore.controller;

import mancio.bookstore.model.Publisher;
import mancio.bookstore.repository.PublisherRepository;
import mancio.bookstore.view.PublisherView;

public class BookStoreController {

	private PublisherRepository publisherRepository;
	private PublisherView publisherView;

	public BookStoreController(PublisherRepository publisherRepository, PublisherView publisherView) {
		this.publisherRepository = publisherRepository;
		this.publisherView = publisherView;

	}

	public void findAllPublishers() {
		publisherView.showAllPublishers(publisherRepository.findAll());
	}

	public void addNewPublisher(Publisher publisher) {
		Publisher foundPublisher = publisherRepository.findById(publisher.getId());
		if (foundPublisher == null) {
			publisherRepository.save(publisher);
			publisherView.publisherAdded(publisher);
		} else {
			publisherView.showError("Already existing publisher with id " + foundPublisher.getId());
		}
	}

	public void deletePublisher(int id) {
		Publisher foundPublisher = publisherRepository.findById(id);
		if (foundPublisher != null) {
			publisherRepository.delete(foundPublisher.getId());
			publisherView.publisherRemoved(foundPublisher);
		}else
			publisherView.showError("No publisher with id " + id);
	}

}
