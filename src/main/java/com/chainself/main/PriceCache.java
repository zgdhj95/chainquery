package com.chainself.main;

import java.util.concurrent.ConcurrentHashMap;

public class PriceCache {

	private static ConcurrentHashMap<String, Double> priceMap = new ConcurrentHashMap<String, Double>();

	public void savePrice(String market, String chain, String unit, Double price) {
		String key = (market + "_" + chain + "_" + unit).toUpperCase();
		priceMap.put(key, price);
	}

	public Double getPrice(String market, String chain, String unit) {
		String key = (market + "_" + chain + "_" + unit).toUpperCase();
		return priceMap.get(key);
	}
}
