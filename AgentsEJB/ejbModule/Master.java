import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

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
public class Master extends Agent {

	private static final long serialVersionUID = 1L;
	//private Map<String, Integer> map = new HashMap<>();
	private ArrayList<Agent> slaves = new ArrayList<>();
	private Map<Character, Integer> mapReduce = new HashMap<>();
	private int delivered = 0;

	public Master(){
		super();
	}

	public Master(AID id){
		super(id);
	}

	@Override
	public void handleMessage(ACLMessage msg){
		if(msg.getPerformative().equals(Performative.REQUEST)){
			Container.getInstance().log("[REQUEST] to Master agent: " + msg.getContent());
			String directoryPath = msg.getContent();

			//get files
			File folder = new File(directoryPath);
			System.out.println(folder);
			File[] documents = folder.listFiles();
			System.out.println(documents);
			Container.getInstance().log("Number of documents: " + documents.length);

			//create slaves
			for(int i=0; i<documents.length; i++){
				AgentBeanRemote abr = findAB();
				Agent a = abr.runAgent("MapReduce$Slave", "Slave"+i);
				slaves.add(a);
			}
			for(int i=0; i<documents.length; i++){
				if(documents[i].isFile()){
					ACLMessage message = new ACLMessage(Performative.REQUEST);
					AID slaveAID = slaves.get(i).getId();
					message.setSender(getId());
					message.addReceiver(slaveAID);
					String document = directoryPath + "/" + documents[i].getName();
					message.setContent(document);
					MessageBeanRemote mbr = findMB();
					mbr.sendMessage(message);
				}
			}			
		}
		else if(msg.getPerformative().equals(Performative.INFORM)){
			delivered++;
			String senderName = msg.getSender().getName();
			Container.getInstance().log("[INFORM] to Master agent from: " + senderName + " : " + msg.getContent());
			
			parseResponse(msg.getContent());
			
			if(delivered == slaves.size()) {
				Container.getInstance().log("Total statistics: " + formStatistics());
			}
			
			/*String stringMap = msg.getContent();
			Properties props = new Properties();
			try{
				props.load(new StringReader(stringMap.substring(1, stringMap.length()-1).replace(",", "\n")));
				for(Map.Entry<Object, Object> e: props.entrySet()){
					String key = (String)e.getKey();
					Integer value = Integer.parseInt((String)e.getValue());
					if(map.containsKey(key)){
						map.put(key, map.get(key) + value);
					}
					else{
						map.put(key, value);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			TreeMap<String, Integer> sortedMap = sortMapByValue(map);
			Container.getInstance().log("---------------");
			if(sortedMap.size() >= 10) {
				int counter = 0;
				Iterator<Entry<String, Integer>> it = sortedMap.entrySet().iterator();
				while(it.hasNext()){
					if(counter==10)
						break;
					Container.getInstance().log(it.next().toString());
					++counter;
				}
			}
			Container.getInstance().log("---------------");*/
		}		
	}
	
	private void parseResponse(String input) {
		String splits[] = input.split("\n");
		for (int i = 1; i < splits.length; i++) {
			Character c = splits[i].charAt(0);
			int count = Integer.parseInt(splits[i].split(":")[1]);

			if (mapReduce.containsKey(c)) {
				mapReduce.put(c, mapReduce.get(c) + count);
			} else {
				mapReduce.put(c, count);
			}
		}
	}
	
	private String formStatistics() {
		String retVal = "MapReduce:";
		for (Map.Entry<Character, Integer> entry : mapReduce.entrySet()) {
			retVal += "\n" + entry.getKey() + ":" + entry.getValue();
		}
		return retVal;
	}

	private TreeMap<String, Integer> sortMapByValue(Map<String, Integer> map2) {
		Comparator<String> comparator = this.new ValueComparator(map2);
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		result.putAll(map2);
		return result;
	}

	private class ValueComparator implements Comparator<String>{
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		public ValueComparator(Map<String, Integer> map){
			this.map.putAll(map);
		}

		@Override
		public int compare(String s1, String s2){
			if(map.get(s1)>=map.get(s2))
				return -1;
			else
				return 1;
		}
	}

}
