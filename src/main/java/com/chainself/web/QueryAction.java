package com.chainself.web;

import javax.servlet.ServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.chainself.cache.PriceCache;

@RestController
public class QueryAction {

	@RequestMapping(value = "/query", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public JSONObject query(ServletRequest request) {
		JSONObject result = new JSONObject();
		result.put("status", "error");
		System.out.println("query=====");
		String market = request.getParameter("market");
		String chain = request.getParameter("chain");
		String unit = request.getParameter("unit");
		if (market == null || "".equals(market) || chain == null || "".equals(chain) || unit == null
				|| "".equals(unit)) {
			result.put("msg", "param is empty");
			return result;
		}
		String key = market + "_" + chain + unit;
		System.out.println("query:" + key);
		try {
			JSONObject json = PriceCache.getPrice(market, chain, unit);
			if (json != null) {
				System.out.println("chain is:" + json.toJSONString());
				return json;
			} else {
				result.put("msg", "chain not exists:" + key);
				return result;
			}
		} catch (Exception e) {
			result.put("msg", e.getMessage());
			e.printStackTrace();
			return result;
		}
	}
}
