package com.example.wifimagager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import data.DBManager;
import data.MyWifiInfo;

public class MyRadioGroup extends Activity {

	private Button btnCancel, btnSave;
	private RadioGroup rg;
	private View rd_btVIew;
	private DBManager mgr;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_radio_group);
		Intent intent = getIntent();
		bundle = intent.getExtras();
		this.getItems();
		setRadioValue();
		this.setEvents();
	}

	private void setRadioValue() {
		int priority = bundle.getInt("priority");
		switch (priority) {
		case 3:
			this.rg.check(R.id.radio_hign);
			break;
		case 2:
			this.rg.check(R.id.radio_mid);
			break;
		case 1:
			this.rg.check(R.id.radio_low);
			break;
		case 0:
			this.rg.check(R.id.radio_shield);
			break;
		case -1:
			this.rg.check(R.id.radio_no);
			break;
		default:
			this.rg.check(R.id.radio_no);
			break;
		}
	}

	private void getItems() {
		this.btnCancel = (Button) findViewById(R.id.btn_cancel);
		rd_btVIew = findViewById(R.id.rd_bt_view);
		this.rg = (RadioGroup) findViewById(R.id.my_radioGroup);
		this.btnSave = (Button) findViewById(R.id.my_btn_save);
		this.mgr = new DBManager(MyRadioGroup.this);
	}

	private void setEvents() {
		this.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		this.btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = bundle.getString("name");
				int netID = bundle.getInt("netID");
				int from = bundle.getInt("from");
				String belong = null;
				int priority = 0;
				switch (rg.getCheckedRadioButtonId()) {
				case R.id.radio_hign:
					belong = "first";
					priority = 3;
					break;
				case R.id.radio_mid:
					belong = "first";
					priority = 2;
					break;
				case R.id.radio_low:
					belong = "first";
					priority = 1;
					break;
				case R.id.radio_shield:
					belong = "last";
					priority = -1;
					break;
				case R.id.radio_no:
					belong = "no";
					priority = -2;
					break;
				default:
					break;
				}

				if (!belong.equals("no")) {
					if (mgr.updatePriorityAndBelong(name, netID, priority,
							belong)) {
						Toast.makeText(
								MyRadioGroup.this.getApplicationContext(),
								"wifi信息已经更新", Toast.LENGTH_SHORT).show();
					} else {
						MyWifiInfo wifi = new MyWifiInfo(name, netID, priority,
								belong);
						if (mgr.addOne(wifi)) {
							Toast.makeText(
									MyRadioGroup.this.getApplicationContext(),
									"wifi信息已经保存", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					mgr.deleteWifiByName(name);
					Toast.makeText(MyRadioGroup.this.getApplicationContext(),
							"wifi信息已经更新", Toast.LENGTH_SHORT).show();
				}
				Intent data = new Intent();
				data.putExtra("id", netID);
				data.putExtra("priority", priority);
				if (from == 1) {
					setResult(1, data);
				} else if (from == 2) {
					setResult(2, data);
				}
				finish();
			}

		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// finish();
		if (ev.getX() >= 0 && ev.getY() >= 0
				&& ev.getX() <= rd_btVIew.getWidth()
				&& ev.getY() <= rd_btVIew.getHeight()) {
			return true;
		}
		finish();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_radio_group, menu);
		return true;
	}

}
