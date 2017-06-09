package session;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
		//Kada agentski centar prihvati poruku od klijenta on 
		//treba da uposli određenog agenta da izvrši zadatak. Upotrebom
		//JMSa centar ispaljuje poruku koju prihvata MDBConsumer. 
		//Consumer vrši lookup za agenta za kog je poruka
		//namenjena i delegira mu poruku.
		Context context;
		try {
			context = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory)context.lookup("java:/ConnectionFactory");
			final Queue target = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();

			Connection con = factory.createConnection();
			try{
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(target);
				System.out.println("----------1---------");
				System.out.println(message);
				producer.send(session.createObjectMessage(message));
				producer.close();
				session.close();
				con.close();
			}
			finally{
				con.close();
			}

		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}
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
