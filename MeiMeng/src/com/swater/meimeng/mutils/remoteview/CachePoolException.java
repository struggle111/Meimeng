package com.swater.meimeng.mutils.remoteview;

/**
 * 缓存管理器异常
 * 
 *
 * @version 1.0
 */
public class CachePoolException extends Exception {
	private static final long serialVersionUID = -9194135039508066111L;

	public CachePoolException() {
		super();
	}

	public CachePoolException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CachePoolException(String detailMessage) {
		super(detailMessage);
	}

	public CachePoolException(Throwable throwable) {
		super(throwable);
	}
	
}
