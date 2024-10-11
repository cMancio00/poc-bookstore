package mancio.bookstore.controller;

import java.util.List;

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

}
