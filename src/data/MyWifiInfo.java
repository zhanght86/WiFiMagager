package data;

public class MyWifiInfo {

	private String name;
	private int net_id;
	private int priority;
	private String belong;

	public MyWifiInfo(String name, int net_id, int priority, String belong) {
		this.name = name;
		this.net_id = net_id;
		this.priority = priority;
		this.belong = belong;
	}

	public MyWifiInfo() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNet_id() {
		return net_id;
	}

	public void setNet_id(int net_id) {
		this.net_id = net_id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

}
