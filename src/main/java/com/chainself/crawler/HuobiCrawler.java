package com.chainself.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chainself.main.PriceCache;

import io.itit.itf.okhttp.FastHttpClient;
import io.itit.itf.okhttp.Response;
import io.itit.itf.okhttp.callback.Callback;
import okhttp3.Call;

/**
 * 火币爬虫
 * 
 * @author yejianfei
 */

public class HuobiCrawler extends java.util.TimerTask {
	private static Logger logger = LoggerFactory.getLogger(HuobiCrawler.class);

	private final static String HUOBI_API_BASE = "https://api.huobi.pro/market/detail/merged?symbol=";
	private final static String HUOBI_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

	public static void query(String chain, String unit) throws Exception {
		String url = HUOBI_API_BASE + chain + unit;
		FastHttpClient.get().addHeader("user-agent", HUOBI_AGENT).url(url).build().executeAsync(new Callback() {
			@Override
			public void onFailure(Call call, Exception e, long id) {
				logger.error("get huobi error!", e);
			}

			@Override
			public void onResponse(Call call, Response response, long id) {
				try {
					String responseStr = response.body().string();
					JSONObject responseJson = (JSONObject) JSON.toJSON(responseStr);
					if ("ok".equals(responseJson.getString("status"))) {
						String chain = responseJson.getString("ch").replaceAll("market.", "")
								.replaceAll(".detail.merged", "");
						String chainKey = "huobi_" + chain;
						PriceCache.savePrice(chainKey, (JSONObject) responseJson.get("tick"));
					}
				} catch (Exception e) {
				}
			}
		});
	}

	@Override
	public void run() {
		long time0 = System.currentTimeMillis();
		try {
			for (String chain : usdtChains) {
				query(chain, "usdt");
			}
			for (String chain : btcChains) {
				query(chain, "btc");
			}
			for (String chain : ethChains) {
				query(chain, "eth");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		long time1 = System.currentTimeMillis();
		System.out.println("  query time " + (time1 - time0));
	}

	private static String[] usdtChains = new String[] { "btc", "bch", "eth", "etc", "ltc", "eos", "xrp", "omg", "dash",
			"zec", "ht", "iost", "dta", "neo", "theta", "hsr", "zil", "qtum", "xem", "smt", "itc", "ela", "snt", "ruff",
			"ven", "let", "elf", "nas", "mds", "gnt", "storj", "cvc", "trx" };

	private static String[] btcChains = new String[] { "bch", "eth", "ltc", "etc", "eos", "omg", "xrp", "dash", "zec",
			"ont", "hsr", "ht", "abt", "ela", "iost", "theta", "edu", "ocn", "zil", "btm", "gas", "neo", "dta", "trx",
			"wpr", "yee", "nas", "xem", "let", "wax", "swftc", "qun", "mds", "itc", "wicc", "act", "zla", "dgd", "ven",
			"srn", "chat", "snc", "smt", "dbc", "tnb", "stk", "qash", "soc", "mee", "icx", "ruff", "qtum", "aidoc",
			"topc", "cmt", "propy", "snt", "elf", "gnx", "blz", "knc", "dat", "eko", "mtx", "utk", "rpx", "storj",
			"lun", "gnt", "cvc", "pay", "mana", "mco", "mtn", "tnt", "ast", "appc", "lsk", "ost", "rcn", "qsp", "zrx",
			"mtl", "powr", "salt", "bat", "rdn", "link", "eng", "req", "evx", "adx", "bcx", "bifi", "bcd", "sbtc",
			"btg" };

	private static String[] ethChains = new String[] { "eos", "omg", "ont", "ht", "abt", "theta", "ela", "wpr", "hsr",
			"zil", "btm", "iost", "edu", "ocn", "nas", "dta", "trx", "wax", "zla", "elf", "ruff", "itc", "ven", "let",
			"mds", "gas", "wicc", "act", "blz", "swftc", "stk", "srn", "chat", "smt", "icx", "yee", "qash", "cmt",
			"dbc", "soc", "snc", "qtum", "aidoc", "tnb", "mee", "dgd", "mtn", "propy", "gnx", "pay", "topc", "qun",
			"utk", "eko", "mtx", "gnt", "dat", "appc", "cvc", "tnt", "mana", "qsp", "lun", "ost", "mco", "lsk", "bat",
			"rcn", "powr", "salt", "rdn", "eng", "link", "req", "evx", "adx" };
}
