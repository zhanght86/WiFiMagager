package com.wifi.map;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;

import com.example.wifimagager.WifiMapActivity;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;

import data.WifiMapData;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> overlayItems = null;
	private List<TencentMapLBSApiResult> mLbsRes = null;
	private List<WifiMapData> wifiMapDatas = null;
	private Context context;

	public MyItemizedOverlay(Context mContext) {
		super(mContext);
		this.context = mContext;
		this.overlayItems = new ArrayList<OverlayItem>();
		this.setOnFocusChangeListener(new com.tencent.tencentmap.mapsdk.map.ItemizedOverlay.OnFocusChangeListener() {

			@Override
			public void onFocusChanged(ItemizedOverlay<?> oldlay,
					OverlayItem newlay) {
				// TODO Auto-generated method stub
				if (newlay != null) {
					new AlertDialog.Builder(context,
							AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
							.setIcon(null).setTitle("详细信息")
							.setMessage(newlay.getSnippet())
							.setInverseBackgroundForced(true)
							.setCancelable(true).setPositiveButton("知道啦", null)
							.show();
				}
			}

		});
		// TODO Auto-generated constructor stub
	}

	public List<TencentMapLBSApiResult> getLbsRes() {
		return mLbsRes;
	}

	public void setLbsRes(List<TencentMapLBSApiResult> lbsRes) {
		this.mLbsRes = lbsRes;
		this.overlayItems.clear();
		for (TencentMapLBSApiResult res : mLbsRes) {
			if (res != null) {
				overlayItems.add(new OverlayItem(
						new GeoPoint((int) (res.Latitude * 1E6),
								(int) (res.Longitude * 1E6)), "p",
						WifiMapActivity.resultToString(res)));
			}
		}
		populate();
	}

	public void setWifiMapInfos(List<WifiMapData> wifiMapDatas) {
		if (wifiMapDatas != null) {
			this.wifiMapDatas = wifiMapDatas;
			this.overlayItems.clear();
			for (WifiMapData res : this.wifiMapDatas) {
				if (res != null) {
					overlayItems.add(new OverlayItem(new GeoPoint((int) (res
							.getLat() * 1E6), (int) (res.getLng() * 1E6)), "p",
							"wifi地址:" + res.getTitle()));
				}
			}
		}
		overlayItems
				.add(new OverlayItem(
						new GeoPoint(
								(int) (WifiMapActivity.mLocRes.Latitude * 1E6),
								(int) (WifiMapActivity.mLocRes.Longitude * 1E6)),
						"mylocation",
						"我在:"
								+ WifiMapActivity
										.resultToString(WifiMapActivity.mLocRes)));
		populate();
	}

	@Override
	protected OverlayItem createItem(int pos) {
		// TODO Auto-generated method stub
		if (overlayItems == null) {
			return null;
		}
		return overlayItems.get(pos);
	}

	public ReGeocoderResult searchFromGeo(GeoPoint geoPoint) {
		GeocoderSearch geocoderSearch = new GeocoderSearch(context);
		try {
			return geocoderSearch.searchFromLocation(geoPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if (overlayItems == null) {
			return 0;
		}
		return overlayItems.size();
	}

}
