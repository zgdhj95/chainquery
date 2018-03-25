package com.chainself.main;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, eiather express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.threadPool;

import java.util.Timer;

import org.springframework.core.env.AbstractEnvironment;

import com.alibaba.fastjson.JSONObject;
import com.chainself.crawler.BinanceCrawler;
import com.chainself.crawler.HuobiCrawler;

import io.itit.itf.okhttp.FastHttpClient;

/**
 * An HTTP server that sends back the content of the received HTTP request in a
 * pretty plaintext form.
 */
public class ChainServer {

	public static void main(String[] args) throws Exception {

		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
		FastHttpClient.okHttpClient.dispatcher().setMaxRequestsPerHost(10);
		startTimer();
		startSparkHttpServer();

	}

	/**
	 * 启动定时器，定时打印心跳信息，获取微信的accessToken。
	 */
	private static void startTimer() {
		Timer queryTimer = new Timer();
		queryTimer.schedule(new HuobiCrawler(), 1000, 10000);
		queryTimer.schedule(new BinanceCrawler(), 3000, 10000);
	}

	public static void startSparkHttpServer() throws Exception {
		int maxThreads = 10;
		threadPool(maxThreads);
		port(80);
		get("/query", (req, res) -> {
			String market = req.queryParams("market");
			String chain = req.queryParams("chain");
			String unit = req.queryParams("unit");
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
		});
	}

}