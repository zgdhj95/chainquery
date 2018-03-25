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
import static spark.Spark.setPort;

import java.util.Timer;

import org.springframework.core.env.AbstractEnvironment;

import com.chainself.crawler.HuobiCrawler;

import io.itit.itf.okhttp.FastHttpClient;
import spark.Request;
import spark.Response;
import spark.Route;

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
	}

	public static void startSparkHttpServer() throws Exception {
		setPort(80);
		get(new Route("/query") {
			@Override
			public Object handle(Request request, Response response) {
				String market = request.queryParams("market");
				String chain = request.queryParams("chain");
				String unit = request.queryParams("unit");
				if (market == null || "".equals(market) || chain == null || "".equals(chain) || unit == null
						|| "".equals(unit)) {
					return "";
				}
				try {
					return PriceCache.getPrice(market, chain, unit).toJSONString();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}
		});
	}

}