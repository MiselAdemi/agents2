import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.MessageBeanRemote;

@Stateful
@Remote(Agent.class)
public class Pong extends Agent {

	private static final long serialVersionUID = 1L;

	private String nodeName;

	public Pong() {
		super();
	}

	public Pong(AID id) {
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage message) {
		System.out.println("Message to Pong: " + message);
		ACLMessage reply = new ACLMessage(Performative.INFORM);
		reply.addReceiver(message.getReplyTo()!=null ? message.getReplyTo():message.getSender());
		reply.setContent("test Pong");
		HashMap<String, Object> userArgs = new HashMap<>();
		userArgs.put("pongCreatedOn", nodeName);
		userArgs.put("pongWorkingOn", "pongworkingon");
		reply.setUserArgs(userArgs);
		MessageBeanRemote messageBean = findMB();
		System.out.println(reply);
		//messageBean.sendMessage(reply);
	}

}
