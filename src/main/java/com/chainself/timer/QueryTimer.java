package com.chainself.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.itf.okhttp.FastHttpClient;
import io.itit.itf.okhttp.Response;
import io.itit.itf.okhttp.callback.Callback;
import okhttp3.Call;

public class QueryTimer extends java.util.TimerTask {

	private static Logger logger = LoggerFactory.getLogger(QueryTimer.class);

	@Override
	public void run() {
		FastHttpClient.get().url("http://123.207.241.107/query?market=huobi&chain=aidoc&unit=btc").build()
				.executeAsync(new Callback() {
					@Override
					public void onFailure(Call call, Exception e, long id) {
						logger.error("get huobi error!", e);
					}

					@Override
					public void onResponse(Call call, Response response, long id) {
						try {

							String responseStr = response.body().string();
							System.out.println("query proxy result=" + responseStr);
							// JSONObject responseJson = (JSONObject) JSON.parse(responseStr);
							// if ("ok".equals(responseJson.getString("status"))) {
							// String chain = responseJson.getString("ch").replaceAll("market.", "")
							// .replaceAll(".detail.merged", "");
							// String chainKey = "huobi_" + chain;
							// PriceCache.savePrice(chainKey, (JSONObject) responseJson.get("tick"));
							// }
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		System.out.println("start query " + System.currentTimeMillis());
	}
}
