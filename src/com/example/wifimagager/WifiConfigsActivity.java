package com.example.wifimagager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.DBManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class WifiConfigsActivity extends Activity {

	private Bundle bundle;
	private ListView listview;
	private int[] id = null;
	private int[] prioritys = null;
	private List<String> list = null;
	private List<String> belongs = null;
	private int sPos = 0;
	private SimpleAdapter adapter;
	private List<Map<String, String>> listMap;
	private WifiAdmin wifiAdmin;
	private DBManager mgr;

	// private ProgressDialog mProDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_search_listview);
		overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
		this.wifiAdmin = new WifiAdmin(this);
		this.mgr = new DBManager(this);
		Intent intent = getIntent();
		bundle = intent.getExtras();
		this.id = bundle.getIntArray("id");
		this.list = bundle.getStringArrayList("name");
		this.belongs = bundle.getStringArrayList("belongs");
		this.prioritys = bundle.getIntArray("prioritys");
		this.listview = (ListView) findViewById(android.R.id.list);
		getData(bundle);
		adapter = new SimpleAdapter(this, listMap, R.layout.my_config_wifis,
				new String[] { "name", "belongs" }, new int[] {
						R.id.tv_wifi_config_name, R.id.tv_wifi_config_belong });
		this.listview.setAdapter(adapter);
		this.listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("from", 1);
				bundle.putString("name", list.get(pos));
				bundle.putInt("netID", id[pos]);
				bundle.putInt("priority", prioritys[pos]);
				sPos = pos;
				intent.putExtras(bundle);
				intent.setClass(WifiConfigsActivity.this, MyRadioGroup.class);
				startActivityForResult(intent, 1);
			}

		});
		this.listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub

				final int mId = id[pos];
				final String name = list.get(pos);
				final int mPos = pos;
				new AlertDialog.Builder(WifiConfigsActivity.this,
						AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setIcon(null)
						.setTitle("wifi管理")
						.setMessage("wifi名称：" + list.get(pos))
						.setInverseBackgroundForced(true).setCancelable(true)
						.setNegativeButton("返回", null)
						.setPositiveButton("弃用该wifi", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								if (wifiAdmin.removeNetworkLink(mId)) {
									mgr.deleteWifiByName(name);
									Toast.makeText(WifiConfigsActivity.this,
											"已经移出wifi信号:" + list.get(mPos),
											Toast.LENGTH_SHORT).show();
									// listMap.remove(list.get(mPos));
									// adapter.notifyDataSetChanged();
								}
							}

						}).show();
				return true;
			}

		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 1 && data != null) {
			int id = data.getIntExtra("id", -1);
			int priority = data.getIntExtra("priority", 0);
			if (id != -1) {
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
				listMap.get(sPos).put("belongs", belong);
				adapter.notifyDataSetChanged();
				Intent dataRes = new Intent();
				dataRes.putExtra("id", id);
				dataRes.putExtra("priority", priority);
				setResult(2, dataRes);
			}
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
	}

	private void getData(Bundle data) {
		listMap = new ArrayList<Map<String, String>>();
		if (list != null) {
			int i = 0;
			for (String str : list) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", str);
				map.put("belongs", belongs.get(i++));
				listMap.add(map);
			}
		}
	}
}
