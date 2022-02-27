package com.filestransfer.receiver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filestransfer.beans.ExecType;
import com.filestransfer.receiver.handler.TransferReceiveHandler;

public class TransferReceiver extends ServerSocket {
	
	private static final Logger logger = LoggerFactory.getLogger(TransferReceiver.class);
	private static final ExecutorService execService = Executors.newCachedThreadPool();

	public TransferReceiver(int port, int backlog, InetAddress bindAddr) throws IOException {
		super(port, backlog, bindAddr);
	}

	static {
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties");
	}

	/**
	 * 入口
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// logger.info(System.getProperty("user.dir"));
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(System.getProperty("user.dir") + File.separator + "config" + File.separator + "FileTransferReceiver.config"));

			Properties properties = new Properties();
			properties.load(bufferedReader);
			String ip = properties.getProperty("ip").strip();
			String port = properties.getProperty("port").strip();
			logger.info("配置文件的ip是：{}，端口号是：{}", ip, port);

			TransferReceiver server = new TransferReceiver(Integer.parseInt(port), 1024, InetAddress.getByName(ip)); // 启动服务端
			server.load();
		} catch (Exception e) {
			logger.error("FileTransferReceiver 捕获异常：", e);
		}
	}

	/**
	 * 使用线程处理每个客户端传输的文件
	 *
	 * @throws Exception
	 */
	public void load() throws Exception {
		logger.info("***接收端即将启动，等待发送端的连接***");
		while (true) {
			// server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
			Socket socket = this.accept();
			/**
			 * 服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后， 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。
			 * 这在并发比较多的情况下会严重影响程序的性能， 为此，可以把它改为如下这种异步处理与客户端通信的方式
			 */
			// 每接收到一个Socket就建立一个新的线程来处理它
			//new Thread(new Task(socket)).start();
			
			execService.execute(new Task(socket));
		}
	}

	/**
	 * 处理客户端传输过来的文件线程类
	 */
	class Task implements Runnable {

		private Socket socket;
		private DataInputStream dis = null;		
		private TransferReceiveHandler receiveHandler = null;
		
		public Task(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				dis = new DataInputStream(socket.getInputStream());

				receiveHandler = new TransferReceiveHandler();
				// 执行动作
				String execType = dis.readUTF();
				if (ExecType.SINGLE_FILE.equalsIgnoreCase(execType.toLowerCase())) {
					receiveHandler.receiveFile(dis, socket);
				}

				if (ExecType.CREATE_DIR.equalsIgnoreCase(execType.toLowerCase())) {
					receiveHandler.createDir(dis, socket);
				}

				if (ExecType.FILE_IN_DIR.equalsIgnoreCase(execType.toLowerCase())) {
					receiveHandler.receiveFileInDir(dis, socket);
				}

				if (ExecType.REMOTE_EXEC_SHELL.equalsIgnoreCase(execType.toLowerCase())) {
					receiveHandler.execShell(dis, socket);
				}

			} catch (Exception e) {
				logger.error("FileTransferReceiver 捕获异常：", e);
			} finally {				
				try {
					if (dis != null) {
						dis.close();
					}					

					socket.close();					
				} catch (Exception e) {
					logger.error("FileTransferReceiver 捕获异常：", e);
					//throw e;
				}
			}
		}
		
	}	

}
