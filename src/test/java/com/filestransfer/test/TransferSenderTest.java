package com.filestransfer.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.filestransfer.bean.ExecType;
import com.filestransfer.protocol.SendFilePacket;
import com.filestransfer.sender.handler.TransferSendHandler;
import com.filestransfer.util.ExceptionUtil;
import com.filestransfer.util.FileUtil;

public class TransferSenderTest {
	private static String protocolHeaderSender;
	private static TransferSendHandler sendHandler;
	private Socket client;

	@Before
	public void before() throws IOException {
		System.out.println("测试前的准备工作。。。");

		BufferedReader bufferedReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + File.separator + "config" + File.separator + "FileTransferSender.config"));

		Properties properties = new Properties();
		properties.load(bufferedReader);
		protocolHeaderSender = properties.getProperty("protocol_header");
		if (StringUtils.isBlank(protocolHeaderSender)) {
			ExceptionUtil.throwException("请先配置协议头信息！");
		}

		protocolHeaderSender = protocolHeaderSender.strip();
	}

	@Test
	public void sendFileTestSendToWindows() throws Exception {
		System.out.println("sendFileTest 开始。。。");
		int port = 8586;
		String ip = "192.168.1.100";
		String file = "D:/test/www.aa.com/配置文件 - 副本.config";
		String targetPath = "D:/test/www.aa.com/mytest04";

		File tempFile = new File(file);
		if (tempFile.exists()) {
			if (tempFile.isDirectory()) {
				ExceptionUtil.throwException("-file参数必须是文件，不能是目录！");
			}

			client = new Socket(ip, port);
			sendHandler = new TransferSendHandler(client);

			SendFilePacket packet = new SendFilePacket(protocolHeaderSender, ExecType.SINGLE_FILE, tempFile.getName(), tempFile.length(),
					FileUtil.getFileMd5(tempFile), targetPath);
			sendHandler.sendFile(tempFile, packet); // 传输文件
		} else {
			ExceptionUtil.throwException("-file参数指定的文件必须存在！");
		}
	}
}
