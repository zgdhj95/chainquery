package io.itit.itf.okhttp.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
	static AtomicLong ai = new AtomicLong(1);

	public static long getId() {
		return ai.getAndIncrement();
	}
}
