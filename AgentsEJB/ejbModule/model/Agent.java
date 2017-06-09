package model;

import java.io.Serializable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import session.MessageBean;
import session.MessageBeanRemote;

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
	
	public void handleMessage(ACLMessage message){}

	public MessageBeanRemote findMB(){
		MessageBeanRemote mbr = new MessageBean();

		try {
			Context context = new InitialContext();
			String remoteName = "java:global/AgentsEAR/AgentsEJB/MessageBean!session.MessageBeanRemote";
			mbr = (MessageBeanRemote)context.lookup(remoteName);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return mbr;
	}

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
