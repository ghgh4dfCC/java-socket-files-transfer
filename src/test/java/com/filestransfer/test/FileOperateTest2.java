package com.filestransfer.test;

import java.io.File;

import org.junit.Test;

public class FileOperateTest2 {
	
	@Test
	public void getAbsolutePathTest01() {
		System.out.println("getAbsolutePathTest01 开始。。。");
		//String targetPath = "D:\\";
		//String targetPath = "D:\\test\\www.aa.com\\123.txt111";
		String targetPath = "D:/test/www.aa.com/mytest01";
		File directory = new File(targetPath);

		System.out.println(directory.getAbsolutePath());
		
		if (directory.exists()) {
			System.out.println("存在");
		}else {
			System.out.println("不存在");
		}

	}
}
