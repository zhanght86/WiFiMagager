package me.http.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.WifiMapData;

public class ParseWifiMapInfo {
	public static List<WifiMapData> getWifiMapInfos(JSONArray array)
			throws JSONException {
		List<WifiMapData> wifiMapInfos = new ArrayList<WifiMapData>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject object = array.optJSONObject(i);
			wifiMapInfos.add(new WifiMapData(object.getInt("id"), object
					.getString("title"), object.getDouble("lat"), object
					.getDouble("lng")));
		}
		return wifiMapInfos;
	}
}
