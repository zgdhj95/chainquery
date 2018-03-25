package com.huobi.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Demo {

	private final static String HUOBI_API_BASE = "https://api.huobi.pro";
	private final static String HUOBI_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

	public static void main(String[] args) throws Exception {
		OkHttpClient client = new OkHttpClient();
		while (true) {
			Request request = new Request.Builder().url(HUOBI_API_BASE + "/market/depth?symbol=ethusdt&type=step0")
					.header("user-agent", HUOBI_AGENT).build();

			Response response = client.newCall(request).execute();
			String restr = response.body().string();
			JSONObject json = (JSONObject) JSON.parse(restr);
			JSONObject tick = json.getJSONObject("tick");
			JSONArray bids = tick.getJSONArray("bids");
			int i = 0;
			for (Object bid : bids) {
				JSONArray bidItem = (JSONArray) bid;
				System.out.println("[买" + i + "] 价格：" + bidItem.getFloatValue(0) + " 数量：" + bidItem.getFloatValue(1));
				i++;
				if (i > 5) {
					break;
				}
			}
			System.out.println("----------- end -----------");
			Thread.sleep(1000);
		}
	}
}
