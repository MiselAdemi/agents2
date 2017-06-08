package model;

public class AgentCenter {
	
	private String alias;
	private String address;
	
	public AgentCenter() {
		super();
	}
	
	public AgentCenter(String alias, String address) {
		super();
		this.alias = alias;
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "AgentCenter [alias=" + alias + ", address=" + address + "]";
	}
}
