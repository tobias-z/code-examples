package io.github.tobiasz.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ThreadUtil {

	public static <T> T waitForMinutes(int minutes, Function<CountDownLatch, T> fn) {
		try {
			CountDownLatch countDownLatch = new CountDownLatch(minutes);
			T result = fn.apply(countDownLatch);
			countDownLatch.await(minutes, TimeUnit.MINUTES);
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException("unable to get result before %s minutes".formatted(minutes));
		}
	}

}
