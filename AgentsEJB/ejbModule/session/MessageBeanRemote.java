package session;

import java.util.ArrayList;

import javax.ejb.Remote;

import model.ACLMessage;

@Remote
public interface MessageBeanRemote {

	/**
	 * Send ACL message
	 */
	public void sendMessage(ACLMessage message);
	
	/**
	 * Get performative list
	 * @return
	 */
	public ArrayList<String> getPerformatives();
}
