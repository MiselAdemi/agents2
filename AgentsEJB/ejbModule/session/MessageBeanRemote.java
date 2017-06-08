package session;

import java.util.ArrayList;

import javax.ejb.Remote;

@Remote
public interface MessageBeanRemote {

	/**
	 * Send ACL message
	 */
	public void sendMessage();
	
	/**
	 * Get performative list
	 * @return
	 */
	public ArrayList<String> getPerformatives();
}
