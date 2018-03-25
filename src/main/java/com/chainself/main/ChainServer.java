package com.chainself.main;

import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.chainself.crawler.BinanceCrawler;
import com.chainself.crawler.HuobiCrawler;

import io.itit.itf.okhttp.FastHttpClient;

/**
 * An HTTP server that sends back the content of the received HTTP request in a
 * pretty plaintext form.
 */
@Controller
@EnableAutoConfiguration
public class ChainServer {

	public static void main(String[] args) throws Exception {

		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
		FastHttpClient.okHttpClient.dispatcher().setMaxRequestsPerHost(10);
		startTimer();
		SpringApplication.run(ChainServer.class, args);
	}

	/**
	 * 启动定时器，定时打印心跳信息，获取微信的accessToken。
	 */
	private static void startTimer() {
		Timer queryTimer = new Timer();
		queryTimer.schedule(new HuobiCrawler(), 1000, 10000);
		queryTimer.schedule(new BinanceCrawler(), 3000, 10000);
	}

	@RequestMapping("/query")
	@ResponseBody
	String query(@RequestParam("market") String market, @RequestParam("chain") String chain,
			@RequestParam("unit") String unit) {
		if (market == null || "".equals(market) || chain == null || "".equals(chain) || unit == null
				|| "".equals(unit)) {
			return "";
		}
		String key = market + "_" + chain + unit;
		System.out.println("query:" + key);
		try {
			JSONObject json = PriceCache.getPrice(market, chain, unit);
			if (json != null) {
				System.out.println("chain is:" + json.toJSONString());
				return json.toJSONString();
			} else {
				return "chain not exists:" + key;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}