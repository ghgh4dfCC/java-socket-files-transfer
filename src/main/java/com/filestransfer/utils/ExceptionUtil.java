package com.filestransfer.utils;

import com.filestransfer.exception.FilesTransferException;

public class ExceptionUtil {
	
	/**
	 * 抛出自定义的异常，在pipeline中可以捕获并终止构建
	 * @param exStr
	 */
	public static void throwException(String exStr) {
		throw new FilesTransferException(exStr);
	}
}
