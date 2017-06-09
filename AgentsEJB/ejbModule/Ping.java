import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.MessageBeanRemote;
import utils.Container;

@Stateful
@Remote(Agent.class)
public class Ping extends Agent {

	private static final long serialVersionUID = 1L;

	private String nodeName;
	
	public Ping() {
		super();
	}
	
	public Ping(AID id) {
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage message) {
		System.out.println("Message to Ping: " + message);
		Container.getInstance().log("Message to Ping: " + message);
		
		if(message.getPerformative().equals(Performative.REQUEST)) {
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(getId());
			msgToPong.addReceiver(message.getReceivers()[0]);
			MessageBeanRemote messageBean = findMB();
			//messageBean.sendMessage(msgToPong);
		}
		else if(message.getPerformative().equals(Performative.INFORM)) {
			ACLMessage messageFromPong = message;
			HashMap<String, Object> args = new HashMap<>(messageFromPong.getUserArgs());
			args.put("pingCreatedOn", nodeName);
			
			System.out.println("Ping-Pong interaction details: ");
			for(Entry<String, Object> e: args.entrySet())
				System.out.println(e.getKey() + " " + e.getValue());
		
			//reply to the original sender (if any)
			if(message.getSender()!=null || message.getReplyTo()!=null){
				ACLMessage reply = new ACLMessage(Performative.INFORM);
				reply.addReceiver(message.getReplyTo()!=null? message.getReplyTo() : message.getSender());
				reply.setContent("PingPong");
				reply.setUserArgs(args);
				MessageBeanRemote messageBean = findMB();
				//messageBean.sendMessage(reply);
			}
		}
	}
	
}
