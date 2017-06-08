package model;

import java.io.Serializable;

public class Agent implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private AgentCenter agentCenter;
	
	public Agent() {
		super();
	}

	public Agent(String id, AgentCenter agentCenter) {
		super();
		this.id = id;
		this.agentCenter = agentCenter;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void handleMessage(){}

	public AgentCenter getAgentCenter() {
		return agentCenter;
	}

	public void setAgentCenter(AgentCenter agentCenter) {
		this.agentCenter = agentCenter;
	};
	
	@Override
	public String toString(){
		return "Agent [id=" + id + "]";
	}

}
