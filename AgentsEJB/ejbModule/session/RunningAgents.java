package session;

import java.util.ArrayList;

import model.Agent;

public class RunningAgents {

	private ArrayList<Agent> runningAgents = new ArrayList<Agent>();

	public ArrayList<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(ArrayList<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}
	
}
