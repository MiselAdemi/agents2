package session;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import model.ACLMessage;
import model.Performative;

@Stateless
@Path("messages")
@LocalBean
public class MessageBean implements MessageBeanRemote {

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public void sendMessage(ACLMessage message) {
		System.out.println(message.toString());
		System.out.println("Hello from sending a message");
	}

	@GET
	@Path("/")
	@Override
	public ArrayList<String> getPerformatives() {
		ArrayList<String> retVal = new ArrayList<>();
		
		for(Performative performative : Performative.values()) {
			retVal.add(performative.toString());
		}
		
		return retVal;
	}

}
