package data;

public class WifiConfigInfo {

	private String name;
	private int net_id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNet_id() {
		return net_id;
	}
	public WifiConfigInfo(String name, int net_id) {
		this.name = name;
		this.net_id = net_id;
	}
	public void setNet_id(int net_id) {
		this.net_id = net_id;
	}
}
