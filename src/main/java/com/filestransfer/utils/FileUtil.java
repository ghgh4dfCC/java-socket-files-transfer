package com.filestransfer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;

public class FileUtil {

	/**
	 * 获取文件的md5值
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String getFileMd5(String filePath) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(new File(filePath));
		String md5 = DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}	
	
	public static String getFileMd5(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String md5 = DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}

}
