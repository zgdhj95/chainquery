package com.chainself.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryTimer extends java.util.TimerTask {

	private static Logger logger = LoggerFactory.getLogger(QueryTimer.class);

	@Override
	public void run() {
		System.out.println("start query " + System.currentTimeMillis());
	}
}
