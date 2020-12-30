package com.github.maxopoly.zeus.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleFuture<T> implements Future<T> {

	private final CountDownLatch latch = new CountDownLatch(1);
	private T value = null;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

	@Override
	public T get() throws InterruptedException {
		latch.await();
		return value;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws TimeoutException {
		try {
			if (latch.await(timeout, unit)) {
				return value;
			} else {
				throw new TimeoutException();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new TimeoutException(e.getMessage());
		}
	}

	public void put(T result) {
		value = result;
		latch.countDown();
	}

}
