import java.util.Random;

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
public class Participant extends Agent {

	private static final long serialVersionUID = 1L;

	public Participant(){
		super();
	}

	public Participant(AID id){
		super(id);
	}


	@Override
	public void handleMessage(ACLMessage msg){
		switch(msg.getPerformative()){
		case CALL_FOR_PROPOSAL:
			Container.getInstance().log(getId().getName() + " has received a message: "/* + msg*/);
			
			Random random = new Random();
			int percentage = random.nextInt(100);
			
			ACLMessage reply = new ACLMessage();
			reply.setSender(getId());
			reply.addReceiver(msg.getReplyTo()!=null? msg.getReplyTo(): msg.getSender());
			
			if(percentage < 50) {
				reply.setPerformative(Performative.REFUSE);
				reply.setProtocol("ContractNet");
				reply.setContent("I do not want to make a bid!");
			}else {
				int bid = random.nextInt(1000);
				reply.setPerformative(Performative.PROPOSE);
				reply.setContent(Integer.toString(bid));
			}
			
			MessageBeanRemote mbr = findMB();
			mbr.sendMessage(reply);
			break;
		case ACCEPT_PROPOSAL:
			String receiverName = getId().getName();
			Container.getInstance().log("Accept Proposal to " + receiverName + ": " + msg.getContent());

			random = new Random();
			percentage = random.nextInt(100);

			reply = new ACLMessage();
			reply.setSender(getId());
			reply.addReceiver(msg.getSender());

			if (percentage < 10) {
				// failure
				reply.setPerformative(Performative.FAILURE);
				reply.setContent("Failed!");
			} else {
				// inform
				reply.setPerformative(Performative.INFORM);
				reply.setContent("Completed!");
			}
			
			mbr = findMB();
			mbr.sendMessage(reply);
			break;
		case REJECT_PROPOSAL:
			receiverName = getId().getName();
			Container.getInstance().log("Reject Proposal to " + receiverName + ": " + msg.getContent());
			break;
		}
	}

}
