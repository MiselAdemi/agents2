import java.util.ArrayList;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import session.AgentBeanRemote;
import session.MessageBeanRemote;
import utils.Container;

@Stateful
@Remote(Agent.class)
public class Initiator extends Agent {

	private static final long serialVersionUID = 1L;
	private int pendingProposals;
	private static final int NUM_PARTICIPANTS = 16;
	
	private int NUMBER_OF_SLAVES = 5;
	private ArrayList<AID> rejectAids = new ArrayList<>();
    private AID acceptAid = null;
    private int bestBid = 1001;
    private int REFUSED = 0;
    private int PROPOSED = 0;

	public Initiator(){
		super();
	}

	public Initiator(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		switch(msg.getPerformative()){
		case REQUEST:
			Container.getInstance().log("[REQUEST]Initiator has received a message: "/* + msg*/);
			AID[] participants = createParticipants();
			ACLMessage message = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
			message.setSender(getId());
			message.setReceivers(participants);
			MessageBeanRemote mbr = findMB();
			mbr.sendMessage(message);
			pendingProposals = participants.length;
			break;
		case REFUSE:
			REFUSED++;
			String senderName = msg.getSender().getName();
			Container.getInstance().log("Refuse to ContractNetMaster from " + senderName + " : " + msg.getContent());
			
			if(REFUSED + PROPOSED == NUM_PARTICIPANTS) {
				deadline();
			}
			
			/*Container.getInstance().log("[ACCEPT_PROPOSAL]Initiator has received a message: );
			--pendingProposals;
			if(pendingProposals == 0){
				Container.getInstance().log("All proposals have been accepted!");
			}*/
			break;
		case PROPOSE:
			PROPOSED++;
			String senderName2 = msg.getSender().getName();
			Container.getInstance().log("Propose to ContractNetMaster from " + senderName2 + " : " + msg.getContent());
			int bid = Integer.parseInt(msg.getContent());
			
			if(bid < bestBid) {
				bestBid = bid;
				if(acceptAid != null) {
					rejectAids.add(acceptAid);
				}
				acceptAid = msg.getSender();
			}else {
				rejectAids.add(msg.getSender());
			}
			
			if(REFUSED + PROPOSED == NUM_PARTICIPANTS) {
				deadline();
			}
			
			break;
		case FAILURE:
			 senderName = msg.getSender().getName();
			 Container.getInstance().log(senderName + " failed to finish: " + msg.getContent());
			break;
		case INFORM:
			senderName = msg.getSender().getName();
			Container.getInstance().log(senderName + " finished successfully: " + msg.getContent());
			break;
		default:
			Container.getInstance().log("Message not understood " /*+ msg*/);
		}
	}

	private AID[] createParticipants() {
		AID[] participants = new AID[NUM_PARTICIPANTS];
		//kreiraj agente
		AgentBeanRemote abr = findAB();

		for(int i=0; i<NUM_PARTICIPANTS; i++){
			Agent a = abr.runAgent("ContractNet$Participant", "Participant" + i);
			participants[i] = a.getId();
		}

		return participants;
	}
	
	private void deadline() {
		if(acceptAid == null) {
			Container.getInstance().log("No proposals");
		}else {
			sendReject();
			sendAccept();
		}
	}
	
	private void sendReject() {
		if (rejectAids.size() > 0) {
			ACLMessage reject = new ACLMessage();
			reject.setPerformative(Performative.REJECT_PROPOSAL);
			reject.setSender(getId());
			for(AID raid : rejectAids) {
				reject.addReceiver(raid);
			}
			reject.setContent("Master rejects Proposal!");
			MessageBeanRemote mbr = findMB();
			mbr.sendMessage(reject);
		}
	}

	private void sendAccept() {
		ACLMessage accept = new ACLMessage();
		accept.setPerformative(Performative.ACCEPT_PROPOSAL);
		accept.setSender(getId());
		accept.addReceiver(acceptAid);
		accept.setContent("Master accepts Proposal!");
		MessageBeanRemote mbr = findMB();
		mbr.sendMessage(accept);
	}

}
