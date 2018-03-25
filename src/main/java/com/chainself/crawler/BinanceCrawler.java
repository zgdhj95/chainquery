package com.chainself.crawler;

import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;

public class BinanceCrawler extends java.util.TimerTask {

	private final static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(BinanceApiKey.API_KEY,
			BinanceApiKey.API_SECRET);
	private final static BinanceApiRestClient client = factory.newRestClient();

	@Override
	public void run() {
		List<TickerPrice> allPriceList = client.getAllPrices();
		int i = 0;
		for (TickerPrice price : allPriceList) {
			i++;
			if (i > 10) {
				return;
			}
			System.out.println(price.getPrice() + " " + price.getSymbol());
		}
	}
}
