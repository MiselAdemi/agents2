package session;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Path("messages")
@LocalBean
public class MessageBean implements MessageBeanRemote {

	@POST
	@Path("/")
	@Override
	public void sendMessage() {
		// TODO Auto-generated method stub
		
	}

	@GET
	@Path("/")
	@Override
	public ArrayList<String> getPerformatives() {
		// TODO Auto-generated method stub
		return null;
	}

}
