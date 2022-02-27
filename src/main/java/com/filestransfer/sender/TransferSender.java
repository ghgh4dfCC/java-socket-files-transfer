package com.filestransfer.sender;

import java.io.File;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.filestransfer.sender.handler.TransferSendHandler;
import com.filestransfer.utils.ExceptionUtil;

public class TransferSender extends Socket {
	private static final Logger logger = Logger.getLogger(TransferSender.class);
	private Socket client;
	
	private static TransferSendHandler sendHandler;
	
	static {
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties");
	}

	/**
	 * 构造函数<br/>
	 * 与服务器建立连接
	 *
	 * @throws Exception
	 */
	public TransferSender(String serverIp, int serverPort) throws Exception {
		super(serverIp, serverPort);
		this.client = this;
		logger.info("Cliect[port:" + client.getLocalPort() + "] 成功连接服务端");
		
		sendHandler = new TransferSendHandler(client);
	}

	/**
	 * 入口
	 *
	 * @param args main函数参数说明：-port 端口号；-ip 需要建立通信的目标机器ip；-file 表示需要传输的单个文件（完整路径）；
	 *             -dir 表示需要传输的目录（完整路径）；-targetPath 表示需要在服务器/目标机器放置文件或目录的目标目录
	 */
	public static void main(String[] args) {
		try {
			int port = 0;
			String ip = "";
			String file = "";
			String dir = "";
			String targetPath = "";
			String shell = "";
			String filter = "";
			boolean portReadyAssign = false;
			boolean ipReadyAssign = false;
			boolean fileReadyAssign = false;
			boolean dirReadyAssign = false;
			boolean targetPathReadyAssign = false;
			boolean shellReadyAssign = false;
			boolean filterReadyAssign = false;

			for (String arg : args) {
				if ("-port".toLowerCase().equals(arg.toLowerCase())) {
					portReadyAssign = true;
					continue;
				}
				if (port == 0 && portReadyAssign) {
					port = Integer.parseInt(arg);
					portReadyAssign = false;
				}

				if ("-ip".toLowerCase().equals(arg.toLowerCase())) {
					ipReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(ip) && ipReadyAssign) {
					ip = arg;
					ipReadyAssign = false;
				}

				if ("-file".toLowerCase().equals(arg.toLowerCase())) {
					fileReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(file) && fileReadyAssign) {
					file = arg;
					fileReadyAssign = false;
				}

				if ("-dir".toLowerCase().equals(arg.toLowerCase())) {
					dirReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(dir) && dirReadyAssign) {
					dir = arg;
					dirReadyAssign = false;
				}

				if ("-targetPath".toLowerCase().equals(arg.toLowerCase())) {
					targetPathReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(targetPath) && targetPathReadyAssign) {
					targetPath = arg;
					targetPathReadyAssign = false;
				}

				if ("-shell".toLowerCase().equals(arg.toLowerCase())) {
					shellReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(shell) && shellReadyAssign) {
					shell = arg;
					shellReadyAssign = false;
				}

				if ("-filter".toLowerCase().equals(arg.toLowerCase())) {
					filterReadyAssign = true;
					continue;
				}
				if (StringUtils.isBlank(filter) && filterReadyAssign) {
					filter = arg;
					filterReadyAssign = false;
				}
			}

//			port = 8586;
//			ip = "192.168.1.100";
//			//file = "D:/test/www.aa.com/123.txt";
//			dir = "D:/test/www.aa.com/mytest01";
//			targetPath = "D:/test/www.aa.com/mytest02";
			//shell="date";

			logger.info(
					MessageFormat.format("当前的调用参数是：port:{0}, ip:{1}, file:{2}, dir:{3}, targetPath:{4}, shell:{5}", port, ip, file, dir, targetPath, shell));

			if (StringUtils.isBlank(ip)) {
				//logger.info("参数ip不可为空！");
				//return;
				ExceptionUtil.throwException("参数ip不可为空！");
			}
			if (port == 0) {
				ExceptionUtil.throwException("参数port不可为0！");
			}

			//
			//!file.equals("") && !dir.equals("")
			if (StringUtils.isNoneBlank(file,dir)) {
				ExceptionUtil.throwException("不能同时给参数-file和-dir赋值！");
			}

			if (StringUtils.isNoneBlank(file, shell)) {
				ExceptionUtil.throwException("不能同时给参数-file和-shell赋值！");
			}

			if (StringUtils.isNoneBlank(dir, shell)) {
				ExceptionUtil.throwException("不能同时给参数-dir和-shell赋值！");
			}

			if (StringUtils.isNotBlank(file) && StringUtils.isBlank(targetPath)) {
				ExceptionUtil.throwException("targetPath参数不可为空！");
			}

			if (StringUtils.isNotBlank(dir) && StringUtils.isBlank(targetPath)) {
				ExceptionUtil.throwException("targetPath参数不可为空！");
			}

			if (StringUtils.isAllBlank(file, dir, shell)) {
				ExceptionUtil.throwException("至少要有一个参数-file或-shell或-dir！");
			}

			// 如果-file参数不为空，则需要判断该文件是否存在，并且判断该文件是否是目录，如果是目录则提示错误
			if (StringUtils.isNotBlank(file)) {
				File tempFile = new File(file);
				if (tempFile.exists()) {
					if (tempFile.isDirectory()) {
						//logger.info("-file参数必须是文件，而不是目录！");
						//return;
						ExceptionUtil.throwException("-file参数必须是文件，而不是目录！");
					}

					TransferSender client = new TransferSender(ip, port); // 启动客户端连接
					//client.sendFile(file, targetPath); // 传输文件
					sendHandler.sendFile(file, targetPath); // 传输文件
				} else {
					ExceptionUtil.throwException("-file参数指定的文件必须存在！");
				}
			}

			// 如果-dir参数不为空，则需要判断该目录是否存在，并且判断该目录是否是目录，如果不是目录则提示错误
			if (StringUtils.isNotBlank(dir)) {
				File tempFile = new File(dir);
				if (tempFile.exists()) {
					if (!tempFile.isDirectory()) {
						ExceptionUtil.throwException("-dir参数必须是目录，而不是文件！");
					}

					List<String> resultFileName = new ArrayList<String>();
					ergodic(tempFile, resultFileName, filter);
					for (String fileName : resultFileName) {
						TransferSender client = new TransferSender(ip, port); // 启动发送端连接
						File tempFile2 = new File(fileName);
						// 如果是一个目录
						if (tempFile2.isDirectory()) {
							String relativePath = fileName.substring((tempFile.getAbsolutePath() + File.separatorChar).length(), fileName.length());
							
							sendHandler.createDir(relativePath, targetPath); // 在目标机器创建目录
						} else {
							String relativePath = fileName.substring((tempFile.getAbsolutePath() + File.separatorChar).length(), fileName.length());
							relativePath = relativePath.substring(0, relativePath.length() - tempFile2.getName().length());
							
							sendHandler.sendFileInDir(fileName, relativePath, targetPath); // 在目标机器创建文件
						}
					}

				} else {
					ExceptionUtil.throwException("-dir参数指定的目录必须存在！");
				}
			}

			// 执行远程命令
			if (StringUtils.isNotBlank(shell)) {
				TransferSender client = new TransferSender(ip, port); // 启动发送端连接
				sendHandler.remoteExecShell(shell); // 在目标机器执行命令
			}

		} catch (Exception e) {
			logger.error(e, e.fillInStackTrace());
		}
	}
	
	/**
	 * 递归获取某个目录下的所有路径
	 * 
	 * @param file
	 * @param resultFileName
	 * @param filters
	 * @return
	 */
	private static List<String> ergodic(File file, List<String> resultFileName, String filters) {
		File[] files = file.listFiles();
		if (files == null)
			return resultFileName;// 判断目录下是不是空的
		for (File f : files) {
			if (f.isDirectory()) {// 判断是否文件夹
				resultFileName.add(f.getPath());
				ergodic(f, resultFileName, filters);// 调用自身,查找子目录
			} else {
				String fileName = f.getName();
				String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (null != filters && !"".equals(filters.strip())) {
					if (!filters.contains(postfix)) {
						resultFileName.add(f.getPath());
					}
				} else {
					resultFileName.add(f.getPath());
				}
			}

		}
		return resultFileName;
	}

}
