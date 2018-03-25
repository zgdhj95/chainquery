package com.chainself.crawler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.chainself.cache.PriceCache;

public class BinanceCrawler extends java.util.TimerTask {

	private final static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(BinanceApiKey.API_KEY,
			BinanceApiKey.API_SECRET);
	private final static BinanceApiRestClient client = factory.newRestClient();

	@Override
	public void run() {
		long time0 = System.currentTimeMillis();
		List<TickerPrice> allPriceList = client.getAllPrices();
		for (TickerPrice price : allPriceList) {
			JSONObject priceJson = new JSONObject();
			priceJson.put("close", price.getPrice());
			PriceCache.savePrice("binance_" + price.getSymbol().toLowerCase(), priceJson);
		}
		long time1 = System.currentTimeMillis();
		System.out.println(" ba query time " + (time1 - time0) + " size=" + PriceCache.priceMap.size());
	}
}
