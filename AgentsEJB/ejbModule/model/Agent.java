package model;

import java.io.Serializable;

public class Agent implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	
	public Agent() {
		super();
	}

	public Agent(String id) {
		super();
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void handleMessage(){};
	
}
