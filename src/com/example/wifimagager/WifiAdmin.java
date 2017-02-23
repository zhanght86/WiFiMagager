package com.example.wifimagager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import data.DBManager;
import data.MyWifiInfo;
import data.WifiConfigInfo;
import data.WifiSearchInfo;

public class WifiAdmin {

	private WifiManager wifi_manager;
	private WifiInfo wifi_info;
	private List<ScanResult> wifi_lists;
	private List<WifiConfiguration> wifi_conifgs;
	private WifiLock wifi_lock;
	private DBManager mgr;

	public enum WifiType {
		WIFI_WEP, WIFI_WPA, WIFI_NOPASS, WIFI_INVALID
	}

	public WifiAdmin(Context context) {
		this.wifi_manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		this.wifi_info = this.wifi_manager.getConnectionInfo();
		this.mgr = new DBManager(context);
	}

	public List<ScanResult> getWifi_lists() {
		return wifi_lists;
	}

	public void setWifi_lists(List<ScanResult> wifi_lists) {
		this.wifi_lists = wifi_lists;
	}

	public boolean wifiUseOrNot() {
		return this.wifi_manager.isWifiEnabled();
	}

	public boolean openWifi() {
		if (!this.wifiUseOrNot()) {
			return this.wifi_manager.setWifiEnabled(true);
		}
		return false;
	}

	public boolean closeWifi() {
		if (this.wifiUseOrNot()) {
			return this.wifi_manager.setWifiEnabled(false);
		}
		return false;
	}

	public int getWifiId() {
		return this.wifi_info == null ? -1 : this.wifi_info.getNetworkId();
	}

	public boolean enableWifiById(int id, boolean bool) {
		return this.wifi_manager.enableNetwork(id, true);
	}

	public boolean disconnectWifi() {
		int wifiId = this.getWifiId();
		if (wifiId == -1) {
			return false;
		}
		this.wifi_manager.disableNetwork(wifiId);
		this.wifi_manager.disconnect();
		this.wifi_info = null;
		return true;
	}

	public void updateConfigInfo() {
		this.scan();
		if (!this.wifi_conifgs.isEmpty()) {
			for (WifiConfiguration wifi : this.wifi_conifgs) {
				if (mgr.isHaveThisWifi(wifi.SSID)) {
					mgr.updateNetid(wifi.SSID, wifi.networkId);
				}
			}
		}
	}

	public int getWifiState() {
		return this.wifi_manager.getWifiState();
	}

	public int getLinkSpeed() {
		return this.wifi_info == null ? 0 : this.wifi_info.getLinkSpeed();
	}

	public void connectWifi() {
		this.wifi_info = this.wifi_manager.getConnectionInfo();
	}

	public void scan() {
		this.wifi_manager.startScan();
		connectWifi();
		this.wifi_lists = this.wifi_manager.getScanResults();
		this.wifi_conifgs = this.wifi_manager.getConfiguredNetworks();
	}

	public String getSSID() {
		return this.wifi_info == null ? null : this.wifi_info.getSSID();
	}

	public int getIpAddress() {
		return this.wifi_info == null ? 0 : this.wifi_info.getIpAddress();
	}

	public List<WifiConfiguration> getWifi_conifgs() {
		return wifi_conifgs;
	}

	public ArrayList<WifiConfigInfo> getWifiConfigInfos() {
		this.updateConfigInfo();
		this.wifi_conifgs = this.wifi_manager.getConfiguredNetworks();
		ArrayList<WifiConfigInfo> results = new ArrayList<WifiConfigInfo>();
		if (null != this.wifi_conifgs && this.wifi_conifgs.size() > 0) {
			for (WifiConfiguration wifi : this.wifi_conifgs) {
				if (wifi.SSID != null && wifi.SSID.length() != 0) {
					results.add(new WifiConfigInfo(wifi.SSID, wifi.networkId));
				}
			}
			return results;
		}
		return null;
	}

	public ArrayList<String> getScanResults() {
		this.scan();
		ArrayList<String> scanResult = new ArrayList<String>();
		if (null != this.wifi_lists) {
			int i = 1;
			for (ScanResult result : wifi_lists) {
				scanResult.add("NO." + (i++) + ":" + result.toString()
						+ "wifi名称" + result.SSID + " wifi强度" + result.level);
			}
			return scanResult;
		} else {
			scanResult.add("当前周围没有可用的wifi");
			return scanResult;
		}
	}

	public Map<String, WifiSearchInfo> getSearchWifiInfo() {
		this.scan();
		Map<String, WifiSearchInfo> wifiMap = new HashMap<String, WifiSearchInfo>();
		if (null != this.wifi_lists) {
			for (ScanResult result : wifi_lists) {
				if (wifiMap.containsKey(result.SSID)
						&& wifiMap.get(result.SSID).getlevel() < result.level) {
					wifiMap.get(result.SSID).setlevel(result.level);
				} else {
					WifiSearchInfo wifi = new WifiSearchInfo(result.SSID,
							result.level);
					wifiMap.put(result.SSID, wifi);
				}
			}
			return wifiMap;
		}
		return null;
	}

	public void createWifiLock() {
		this.wifi_lock = this.wifi_manager.createWifiLock("lock");
	}

	public void acquireWifiLock() {
		if (this.wifi_lock != null) {
			this.wifi_lock.acquire();
		}
	}

	public void releaseWifiLock() {
		if (this.wifi_lock.isHeld()) {
			this.wifi_lock.release();
		}
	}

	public int getIP() {
		return this.wifi_info == null ? 0 : this.wifi_info.getIpAddress();
	}

	public String getWifiInfo() {
		return this.wifi_info == null ? "当前没有连接wifi" : this.wifi_info
				.toString();
	}

	public boolean disableNetWorkLink(int netID) {
		this.wifi_manager.disableNetwork(netID);
		return this.wifi_manager.disconnect();
	}

	public boolean removeNetworkLink(int netID) {
		return this.wifi_manager.removeNetwork(netID);
	}

	public int addNetwork(WifiConfiguration wcg) {
		WifiConfiguration config = this.IsExsits(wcg.SSID);
		if (null != config) {
			this.wifi_manager.removeNetwork(config.networkId);
		}
		int wcgID = this.wifi_manager.addNetwork(wcg);
		this.wifi_manager.enableNetwork(wcgID, true);
		return wcgID;
	}

	public int addNetworkNoLink(WifiConfiguration wcg) {
		WifiConfiguration config = this.IsExsits(wcg.SSID);
		if (null != config) {
			this.wifi_manager.removeNetwork(config.networkId);
		}
		return this.wifi_manager.addNetwork(wcg);
	}

	public Map<String, Object> linkMaxPrioritySingnal() {
		this.scan();
		this.updateConfigInfo();
		List<MyWifiInfo> list;
		list = mgr.query(1);
		int priority = -10;
		int netid = -1;
		String name = null;
		if (!list.isEmpty() && !this.wifi_lists.isEmpty()) {
			for (MyWifiInfo wifi : list) {
				for (ScanResult result : this.wifi_lists) {
					if (wifi.getName().equals('"' + result.SSID + '"')
							&& wifi.getPriority() > priority) {
						priority = wifi.getPriority();
						netid = wifi.getNet_id();
						name = wifi.getName();
					}
				}
			}
		}
		if (name != null && netid != -1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("id", netid);
			return map;
		}
		return null;
	}

	public boolean linkExceptLastWifi() {
		this.scan();
		List<MyWifiInfo> list = mgr.query(0);
		Map<String, Integer> last_list = new HashMap<String, Integer>();
		if (!list.isEmpty()) {
			for (MyWifiInfo wifi : list) {
				last_list.put(wifi.getName(), wifi.getNet_id());
			}
		}
		List<WifiConfigInfo> configs = this.getWifiConfigInfos();
		int level = -1000;
		int netid = -1;
		Map<String, WifiSearchInfo> map = this.getSearchWifiInfo();
		if (!configs.isEmpty() && !map.isEmpty()) {
			for (WifiConfigInfo wifi : configs) {
				if (last_list.containsKey(wifi.getName())) {
					continue;
				}
				for (ScanResult scan : this.wifi_lists) {
					if (wifi.getName().equals(scan.SSID)) {
						if (scan.level > level) {
							level = scan.level;
							netid = wifi.getNet_id();
						}
					}
				}
			}
		}
		if (netid != -1) {
			if (this.enableWifiById(netid, true)) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Object> linkMaxStrongSignal() {
		ArrayList<WifiConfigInfo> results = getWifiConfigInfos();
		Map<String, Integer> names = new HashMap<String, Integer>();
		Map<String, WifiSearchInfo> search = getSearchWifiInfo();
		if (results != null) {
			for (WifiConfigInfo wifi : results) {
				names.put(wifi.getName(), wifi.getNet_id());
			}
		} else {
			return null;
		}
		if (!search.isEmpty()) {
			int maxId = -1;
			int level = -1000;
			String mName = null;
			for (WifiSearchInfo wifi : search.values()) {
				String name = '"' + wifi.getName() + '"';
				if (names.containsKey(name) && (wifi.getlevel() > level)) {
					level = wifi.getlevel();
					maxId = names.get(name);
					mName = name;
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", mName);
			map.put("id", maxId);
			return map;
		} else {
			return null;
		}

	}

	private WifiConfiguration IsExsits(String ssid) {
		this.wifi_conifgs = this.wifi_manager.getConfiguredNetworks();
		for (WifiConfiguration config : this.wifi_conifgs) {
			if (config.SSID.equals(ssid)) {
				return config;
			}
		}
		return null;
	}

}
