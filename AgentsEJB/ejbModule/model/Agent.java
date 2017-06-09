package model;

import java.io.Serializable;

public class Agent implements Serializable {

	private static final long serialVersionUID = 1L;
	private AID id;
	private AgentCenter agentCenter;
	
	public Agent() {
		super();
	}

	public Agent(AID id) {
		super();
		this.id = id;
	}
	
	public AID getId() {
		return id;
	}

	public void setId(AID id) {
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
