package mancio.bookstore.model;

public class Publisher {
	private String id;
	private String name;

	public Publisher() {}
	
	public Publisher(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
