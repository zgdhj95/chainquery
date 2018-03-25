package com.chainself.main;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

public class PriceCache {

	public static ConcurrentHashMap<String, JSONObject> priceMap = new ConcurrentHashMap<String, JSONObject>();

	public static void savePrice(String key, JSONObject price) {
		priceMap.put(key, price);
	}

	public static JSONObject getPrice(String market, String chain, String unit) {
		String key = (market + "_" + chain + unit).toLowerCase();
		return priceMap.get(key);
	}

}
