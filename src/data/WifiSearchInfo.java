package data;

public class WifiSearchInfo {

	private String name;
	private int level;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getlevel() {
		return level;
	}

	public WifiSearchInfo(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public void setlevel(int level) {
		this.level = level;
	}
}
