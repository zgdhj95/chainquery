package com.chainself;

import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

import com.chainself.crawler.BinanceCrawler;
import com.chainself.crawler.HuobiCrawler;

import io.itit.itf.okhttp.FastHttpClient;

/**
 * An HTTP server that sends back the content of the received HTTP request in a
 * pretty plaintext form.
 */
@SpringBootApplication
public class ChainServer {

	public static void main(String[] args) throws Exception {

		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
		FastHttpClient.okHttpClient.dispatcher().setMaxRequestsPerHost(10);
		// startTimer();
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

}