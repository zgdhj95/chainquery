package com.chainself.crawler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.chainself.main.PriceCache;

public class BinanceCrawler extends java.util.TimerTask {

	private final static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(BinanceApiKey.API_KEY,
			BinanceApiKey.API_SECRET);
	private final static BinanceApiRestClient client = factory.newRestClient();

	@Override
	public void run() {
		List<TickerPrice> allPriceList = client.getAllPrices();
		for (TickerPrice price : allPriceList) {
			JSONObject priceJson = new JSONObject();
			priceJson.put("close", price.getPrice());
			PriceCache.savePrice("binance_" + price.getSymbol().toLowerCase(), priceJson);
		}
	}
}
