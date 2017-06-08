package session;

import java.util.HashSet;
import java.util.Set;

import model.AgentCenter;

public class AgentHosts {
	private Set<AgentCenter> hosts = new HashSet<>();

	public Set<AgentCenter> getHosts() {
		return hosts;
	}

	public void setHosts(Set<AgentCenter> hosts) {
		this.hosts = hosts;
	}
	
}
