package data;

public class WifiMapData {
	private int id;
	private String title;
	private double lat;
	private double lng;

	public WifiMapData(int id, String title, double lat, double lng) {
		this.id = id;
		this.title = title;
		this.lat = lat;
		this.lng = lng;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}
}
