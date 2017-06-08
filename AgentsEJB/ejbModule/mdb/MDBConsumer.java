package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(
						propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(
						propertyName = "destination", propertyValue = "queue/mojQueue")	
		})
public class MDBConsumer implements MessageListener {

	public MDBConsumer() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onMessage(Message message) {
		System.out.println("hello, I have received a message");
		System.out.println(message);
	}

}
