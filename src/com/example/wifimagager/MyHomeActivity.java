package com.example.wifimagager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import data.DBManager;
import data.MyWifiInfo;
import data.WifiConfigInfo;

public class MyHomeActivity extends Activity {

	private CheckBox cbOpenWifi, cbOpenService;
	private TextView tvWifiName, tvWifiSpeed, tvWifiPriority, tvChangeWifiConfig;
	private Button btnWifiAround, btnWifiRefesh, btnWifiMap, btnWifiMax, btnWifiPri;
	private View viewSetPriority;
	private WifiAdmin wifiAdmin;
	private DBManager mgr;
	private long endtime = 0;

	// private LinearLayout mLayoutApp, mLayoutGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_home_activity);
		overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
		this.getItems();
		this.setUseInfo();
		this.setCheckBoxOpenWifi();
		this.setWifiInfo();
		this.setEvents();
		StatConfig.setDebugEnable(true);
		StatService.trackCustomEvent(this, "onCreate", "wifimanager");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
		StatService.trackEndPage(this, "home");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setCheckBoxOpenWifi();
		super.onResume();
		StatService.onResume(this);
		StatService.trackBeginPage(this, "home");
	}

	private void setCheckBoxOpenWifi() {
		if (this.wifiAdmin.wifiUseOrNot()) {
			this.cbOpenWifi.setChecked(true);
		} else {
			this.cbOpenWifi.setChecked(false);
		}
	}

	private boolean setWifiInfo() {
		if (this.wifiAdmin.wifiUseOrNot()) {
			this.wifiAdmin.scan();
			// String wifiName = '"' + wifiAdmin.getSSID() + '"';
			String wifiName = wifiAdmin.getSSID();
			int wifiSpeed = this.wifiAdmin.getLinkSpeed();
			int id = wifiAdmin.getWifiId();
			if (wifiName != null && wifiName.trim() != "" && wifiSpeed != -1
					&& id != -1) {
				if (wifiName.charAt(0) != '"') {
					wifiName = '"' + wifiName + '"';
				}
				this.tvWifiName.setText(wifiName);
				this.tvWifiSpeed.setText(Integer.toString(wifiSpeed) + "Mbps");
				MyWifiInfo wifi = mgr.findByNameAndId(wifiName, id);
				if (null != wifi) {
					String belong = "无";
					switch (wifi.getPriority()) {
					case 3:
						belong = "高";
						break;
					case 2:
						belong = "中";
						break;
					case 1:
						belong = "低";
						break;
					case 0:
						belong = "屏蔽";
						break;
					default:
						belong = "无";
						break;
					}
					tvWifiPriority.setText(belong);
				} else {
					tvWifiPriority.setText("无");
				}
				return true;
			}
		}
		return false;
	}

	private void setEvents() {
		this.btnWifiMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyHomeActivity.this, WifiMapActivity.class);
				startActivity(intent);
			}

		});
		this.cbOpenWifi
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							if (wifiAdmin.openWifi()) {
								Toast.makeText(MyHomeActivity.this, "wifi已经打开",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							if (wifiAdmin.closeWifi()) {
								Toast.makeText(MyHomeActivity.this, "wifi已经关闭",
										Toast.LENGTH_SHORT).show();
							}
						}
					}

				});
		this.btnWifiAround.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
			}

		});
		this.viewSetPriority.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!wifiAdmin.wifiUseOrNot()) {
					Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				ArrayList<WifiConfigInfo> configs;
				HashMap<Integer, Integer> map = getMyConfigWifis();
				configs = wifiAdmin.getWifiConfigInfos();
				if (null != configs && configs.size() != 0) {
					ArrayList<String> names = new ArrayList<String>();
					ArrayList<String> belongs = new ArrayList<String>();
					int[] ids = new int[configs.size()];
					int[] prioritys = new int[configs.size()];
					int i = 0;
					if (null != map) {
						for (WifiConfigInfo wifi : configs) {
							names.add(wifi.getName());
							ids[i] = wifi.getNet_id();
							if (map.containsKey(wifi.getNet_id())) {
								int id = map.get(wifi.getNet_id());
								prioritys[i] = id;
								if (id == 3) {
									belongs.add("高");
								} else if (id == 2) {
									belongs.add("中");
								} else if (id == 1) {
									belongs.add("低");
								} else {
									belongs.add("屏蔽");
								}
							} else {
								prioritys[i] = -1;
								belongs.add("无");
							}
							i++;
						}
					}
					bundle.putStringArrayList("name", names);
					bundle.putStringArrayList("belongs", belongs);
					bundle.putIntArray("id", ids);
					bundle.putIntArray("prioritys", prioritys);
					intent.putExtras(bundle);
					intent.setClass(MyHomeActivity.this,
							WifiConfigsActivity.class);
					startActivityForResult(intent, 2);
				}
			}

		});

		this.btnWifiRefesh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!wifiAdmin.wifiUseOrNot()) {
					Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				setCheckBoxOpenWifi();
				if (setWifiInfo()) {
					Toast.makeText(MyHomeActivity.this, "wifi信息已经更新",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MyHomeActivity.this, "请稍候再刷新...",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		this.btnWifiPri.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!wifiAdmin.wifiUseOrNot()) {
					Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				wifiAdmin.scan();
				Map<String, Object> wifiMap = wifiAdmin
						.linkMaxPrioritySingnal();
				if (wifiMap != null) {
					int id = (Integer) wifiMap.get("id");
					if (id != -1) {
						wifiAdmin.enableWifiById(id, true);
						setCheckBoxOpenWifi();
						setWifiInfo();
						Toast.makeText(MyHomeActivity.this,
								"已经连接到当前优先级最高的wifi信号" + wifiMap.get("name"),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(MyHomeActivity.this, "当前没有设置过优先级的wifi信号！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		this.btnWifiMax.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!wifiAdmin.wifiUseOrNot()) {
					Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (MyHomeActivity.isServiceRunning(MyHomeActivity.this, "com.example.wifimagager.WifiService")) {
					Toast.makeText(MyHomeActivity.this, "请先关闭优先级自动连接服务！", Toast.LENGTH_SHORT).show();
					return;
				}
				wifiAdmin.scan();
				Map<String, Object> wifiMap = wifiAdmin.linkMaxStrongSignal();
				if (wifiMap != null) {
					int id = (Integer) wifiMap.get("id");
					if (id != -1) {
						wifiAdmin.enableWifiById(id, true);
						setCheckBoxOpenWifi();
						setWifiInfo();
						Toast.makeText(MyHomeActivity.this, "已经连接到当前最强的wifi信号" + wifiMap.get("name"), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(MyHomeActivity.this, "当前好像没有可用的wifi信号！", Toast.LENGTH_SHORT).show();
				}
			}

		});

		this.cbOpenService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked && !wifiAdmin.wifiUseOrNot()) {
							Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！",
									Toast.LENGTH_SHORT).show();
							buttonView.setChecked(false);
							return;
						}
						if (isChecked && wifiAdmin != null) {
							wifiAdmin.openWifi();
							if (!cbOpenWifi.isChecked())
								cbOpenWifi.setChecked(true);
							if (wifiAdmin.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
								startService(new Intent(
										"com.example.wifimagager.WifiService"));
								Toast.makeText(MyHomeActivity.this,
										"wifi服务已打开", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(MyHomeActivity.this,
										"等wifi打开后再开启服务", Toast.LENGTH_SHORT)
										.show();
								buttonView.setChecked(false);
							}

						} else {
							if (stopService(new Intent(
									"com.example.wifimagager.WifiService"))) {
								Toast.makeText(MyHomeActivity.this,
										"您已成功关闭服务！", Toast.LENGTH_SHORT).show();
							}
						}
					}

				});
		this.tvChangeWifiConfig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!wifiAdmin.wifiUseOrNot()) {
					Toast.makeText(MyHomeActivity.this, "请先打开wifi功能！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				setCheckBoxOpenWifi();
				setWifiInfo();
				wifiAdmin.updateConfigInfo();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				int netID = wifiAdmin.getWifiId();
				// String name = '"' + wifiAdmin.getSSID() + '"';
				String name = wifiAdmin.getSSID();
				if (name.charAt(0) != '"') {
					name = '"' + name + '"';
				}
				if (netID != -1) {
					MyWifiInfo wifi = mgr.findByNameAndId(name, netID);
					if (wifi != null) {
						bundle.putInt("priority", wifi.getPriority());
					} else {
						bundle.putInt("priority", -1);
					}
				} else {
					bundle.putInt("priority", -1);
				}
				bundle.putInt("from", 2);
				bundle.putInt("netID", netID);
				bundle.putString("name", name);
				intent.putExtras(bundle);
				intent.setClass(MyHomeActivity.this, MyRadioGroup.class);
				startActivityForResult(intent, 2);
			}

		});
	}

	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 2 && data != null) {
			int id = data.getIntExtra("id", -1);
			int priority = data.getIntExtra("priority", 0);
			if (id != -1 && id == wifiAdmin.getWifiId()) {
				String belong;
				switch (priority) {
				case 3:
					belong = "高";
					priority = 3;
					break;
				case 2:
					belong = "中";
					priority = 2;
					break;
				case 1:
					belong = "低";
					priority = 1;
					break;
				case -1:
					belong = "屏蔽";
					priority = -1;
					break;
				case -2:
					belong = "无";
					priority = -2;
					break;
				default:
					belong = "无";
					break;
				}
				tvWifiPriority.setText(belong);
			}
		}
	}

	private HashMap<Integer, Integer> getMyConfigWifis() {
		ArrayList<MyWifiInfo> wifiInfos = this.mgr.query(2);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (MyWifiInfo wifi : wifiInfos) {
			map.put(wifi.getNet_id(), wifi.getPriority());
		}
		return map;
	}

	private void setUseInfo() {
		this.wifiAdmin = new WifiAdmin(MyHomeActivity.this);
		this.mgr = new DBManager(MyHomeActivity.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - endtime >= 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				endtime = System.currentTimeMillis();
			} else {
				this.finish();
				System.exit(0);
				return true;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getItems() {
		this.cbOpenWifi = (CheckBox) findViewById(R.id.cb_open_wifi);
		this.tvWifiName = (TextView) findViewById(R.id.tv_wifi_myname);
		this.tvWifiSpeed = (TextView) findViewById(R.id.tv_wifi_myspeed);
		this.tvWifiPriority = (TextView) findViewById(R.id.tv_wifi_mypriority);
		this.btnWifiAround = (Button) findViewById(R.id.btn_wifi_around);
		this.viewSetPriority = (View) findViewById(R.id.view_set_priority);
		this.btnWifiRefesh = (Button) findViewById(R.id.btn_wifi_refresh);
		this.cbOpenService = (CheckBox) findViewById(R.id.cb_open_service);
		this.tvChangeWifiConfig = (TextView) findViewById(R.id.tv_change_wifi_config);
		this.btnWifiMap = (Button) findViewById(R.id.btn_wifi_map);
		this.btnWifiMax = (Button) findViewById(R.id.btn_wifi_strong);
		this.btnWifiPri = (Button) findViewById(R.id.btn_wifi_pri);
		this.cbOpenService.setChecked(isServiceRunning(this,
				"com.example.wifimagager.WifiService"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_test, menu);
		return true;
	}

}
