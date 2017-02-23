package com.example.wifimagager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.http.utils.MyHttpUtils;
import me.http.utils.MyThreadPool;
import me.http.utils.ParseWifiMapInfo;
import me.hzj.global.Constant;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.wifi.map.MyItemizedOverlay;
import com.wifi.map.MyOverlay;

import data.WifiMapData;

public class WifiMapActivity extends MapActivity {

	private static final boolean D = Constant.DEBUG;
	private MapView mapView = null;
	private LocListener mListener = null;
	public static TencentMapLBSApiResult mLocRes = null;
	private TencentMapLBSApiResult mLocRes_old = null;
	private MyOverlay myOverlay = null;
	private MyItemizedOverlay myItemOverlay = null;
	private List<WifiMapData> wifiMapDatas = null;
	private MyHandler mHandler;

	// private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_map);
		overridePendingTransition(R.anim.anim_into, R.anim.anim_back);
		this.myOverlay = new MyOverlay();
		this.myItemOverlay = new MyItemizedOverlay(this);
		this.mapView = (MapView) findViewById(R.id.mapview);
		this.mListener = new LocListener(TencentMapLBSApi.GEO_TYPE_GCJ02,
				TencentMapLBSApi.LEVEL_ADMIN_AREA, 0);
		int req = TencentMapLBSApi.getInstance().requestLocationUpdate(
				this.getApplicationContext(), mListener);
		if (D) {
			Log.i("map", "结果req:" + req);
		}
		mHandler = new MyHandler(this);

		if (mLocRes != null) {
			MyThreadPool.getInstance().getExecutorService()
					.submit(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Map<String, String> params = new HashMap<String, String>();
							params.put("lat", Double.toString(mLocRes.Latitude));
							params.put("lng",
									Double.toString(mLocRes.Longitude));
							JSONArray array = MyHttpUtils.getDatasByUrl(
									"http://wifiok.com/api/hotspots", params);
							if (array != null) {
								try {
									wifiMapDatas = ParseWifiMapInfo
											.getWifiMapInfos(array);
									if (wifiMapDatas != null) {
										mHandler.sendEmptyMessage(1);
									} else {
										mHandler.sendEmptyMessage(0);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								mHandler.sendEmptyMessage(0);
							}
						}

					});
		}
	}

	private void searchSuccess() {
		mapView.clearAllOverlays();
		mapView.getController().setCenter(
				new GeoPoint((int) (mLocRes.Latitude * 1E6),
						(int) (mLocRes.Longitude * 1E6)));
		myOverlay.setLat(mLocRes.Latitude);
		myOverlay.setLng(mLocRes.Longitude);
		mapView.addOverlay(myOverlay);
		mapView.getController().setZoom(14);
		myItemOverlay.setWifiMapInfos(wifiMapDatas);
		mapView.addOverlay(myItemOverlay);
	}

	private void searchRessults(JSONArray obj) {
		try {
			if (obj != null) {
				wifiMapDatas = ParseWifiMapInfo.getWifiMapInfos(obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void searchFail() {
		searchSuccess();
		Toast.makeText(WifiMapActivity.this.getApplicationContext(),
				"对不起，没有查到相关数据！", Toast.LENGTH_SHORT).show();
	}

	private static final class MyHandler extends Handler {
		private final WeakReference<WifiMapActivity> mapActivity;

		private MyHandler(WifiMapActivity msgActivity) {
			this.mapActivity = new WeakReference<WifiMapActivity>(msgActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			WifiMapActivity mMapActivity = mapActivity.get();
			if (mMapActivity != null) {
				switch (msg.what) {
				case 1:
					JSONArray obj = (JSONArray) msg.obj;
					mMapActivity.searchRessults(obj);
					mMapActivity.searchSuccess();
					break;
				case 0:
					mMapActivity.searchFail();
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wifi_map, menu);
		return true;
	}

	public class LocListener extends TencentMapLBSApiListener {

		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locRes) {
			// TODO Auto-generated method stub
			mLocRes_old = mLocRes;
			mLocRes = locRes;
			if ((mLocRes_old == null && mLocRes != null)
					|| (mLocRes != null && cmpTwoRes(mLocRes, mLocRes_old))) {
				MyThreadPool.getInstance().getExecutorService()
						.submit(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Map<String, String> params = new HashMap<String, String>();
								params.put("lat",
										Double.toString(mLocRes.Latitude));
								params.put("lng",
										Double.toString(mLocRes.Longitude));
								JSONArray array = MyHttpUtils.getDatasByUrl(
										"http://wifiok.com/api/hotspots",
										params);
								if (array != null) {
									Message msg = mHandler.obtainMessage(1);
									msg.obj = array;
									mHandler.sendMessage(msg);
									// try {
									// wifiMapDatas = ParseWifiMapInfo
									// .getWifiMapInfos(array);
									// if (wifiMapDatas != null) {
									// mHandler.sendEmptyMessage(1);
									// } else {
									// mHandler.sendEmptyMessage(0);
									// }
									// } catch (JSONException e) {
									// // TODO Auto-generated catch block
									// e.printStackTrace();
									// }
								} else {
									mHandler.sendEmptyMessage(0);
								}
							}

						});
			}
		}

		@Override
		public void onStatusUpdate(int arg0) {
			// TODO Auto-generated method stub
			super.onStatusUpdate(arg0);
		}

	}

	@Override
	public void onBackPressed() {
		// 如果有需要，可以点击后退关闭插播广告。
		super.onBackPressed();
		overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
	}

	@Override
	protected void onStop() {
		// 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private boolean cmpTwoRes(TencentMapLBSApiResult resA,
			TencentMapLBSApiResult resB) {
		if (resA != null && resB != null) {
			if (resA.Latitude - resB.Latitude >= 0.035
					|| resA.Longitude - resB.Longitude >= 0.035) {
				return true;
			}
		}
		return false;
	}

	public static String resultToString(TencentMapLBSApiResult result) {
		StringBuilder sb = new StringBuilder();
		if (result != null) {
			if (result.Province != null && !result.Province.equals("Unknown")) {
				sb.append(result.Province);
			}
			if (result.City != null && !result.City.equals("Unknown")) {
				sb.append(result.City);
			}
			if (result.District != null && !result.District.equals("Unknown")) {
				sb.append(result.District);
			}
			if (result.Town != null && !result.Town.equals("Unknown")) {
				sb.append(result.Town);
			}
			if (result.Village != null && !result.Village.equals("Unknown")) {
				sb.append(result.Village);
			}
			if (result.Street != null && !result.Street.equals("Unknown")) {
				sb.append(result.Street);
			}
			if (result.StreetNo != null && !result.StreetNo.equals("Unknown")) {
				sb.append(result.StreetNo);
			}
		}
		return sb.toString();
	}

}
