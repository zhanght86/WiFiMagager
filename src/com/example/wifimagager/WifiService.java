package com.example.wifimagager;

import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WifiService extends Service {

	private WifiAdmin admin;
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (admin.getWifiState() == WifiManager.WIFI_STATE_DISABLED
					|| admin.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
				Toast.makeText(WifiService.this, "wifi服务正在运行，不能关闭wifi",
						Toast.LENGTH_SHORT).show();
				admin.openWifi();
			} else {
				String action = intent.getAction();
				if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					Log.i("info", "wifi连接发生变化");
					admin.openWifi();
					admin.scan();
					Map<String, Object> map = admin.linkMaxPrioritySingnal();
					if (null != map
							&& admin.getWifiId() != (Integer) map.get("id")) {
						String name = (String) map.get("name");
						int netid = (Integer) map.get("id");
						admin.enableWifiById(netid, true);
						Toast.makeText(context,
								"检测到变化，已连接到当前优先级最高的wifi信号" + name,
								Toast.LENGTH_SHORT).show();
					} else {
						if (admin.linkExceptLastWifi()) {
							Toast.makeText(context, "检测到变化，已连接到当前信号最强的wifi信号",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}

	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		admin = new WifiAdmin(this);
		Map<String, Object> map = admin.linkMaxPrioritySingnal();
		if (null != map) {
			String name = (String) map.get("name");
			int netid = (Integer) map.get("id");
			if (admin.getWifiId() != netid) {
				admin.enableWifiById(netid, true);
				Toast.makeText(this.getApplicationContext(),
						"检测到变化，已经连接到当前优先级最高的wifi信号" + name, Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this.getApplicationContext(),
						"您已经连接到当前优先级最高的wifi信号" + name, Toast.LENGTH_SHORT)
						.show();
			}
		}
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(myReceiver, mFilter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}

}
