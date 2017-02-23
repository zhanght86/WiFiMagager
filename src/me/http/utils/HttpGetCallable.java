package me.http.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

public class HttpGetCallable implements Callable<JSONArray> {

	private final String url;
	private Map<String, String> mapParams;
	private static final int TIMEOUT = 3000;

	public HttpGetCallable(String url, Map<String, String> params) {
		super();
		this.url = url;
		this.mapParams = params;
	}

	@Override
	public JSONArray call() throws Exception {
		// TODO Auto-generated method stub
		HttpClient httpClient = new DefaultHttpClient();
		BasicHttpParams httpParams = new BasicHttpParams();
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : mapParams.entrySet()) {
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		String param = URLEncodedUtils.format(params, "UTF-8");
		HttpGet httpGet = new HttpGet(url + "?" + param);
		httpGet.setParams(httpParams);
		HttpResponse response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String result = EntityUtils.toString(response.getEntity());
			JSONArray resultArray = new JSONArray(result);
			return resultArray;
		} else {
			return null;
		}
	}
}