package session;

import javax.ejb.Remote;

@Remote
public interface AgentCenterBeanRemote {

	public void registerMe();
	
	public void getAllSupportedAgents();
	
	public void forwardNewAgentTypes();
	
	public void forwardRunningAgents();
	
	public void deleteNode(String alias);
	
	public void checkIfAlive();
}
