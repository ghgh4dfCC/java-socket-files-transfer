package com.filestransfer.receiver.handler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.filestransfer.bean.HandleResultBean;
import com.filestransfer.util.FileUtil;

public class TransferReceiveHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferReceiveHandler.class);

	private OutputStreamWriter osw = null;
	private PrintWriter pw = null;
	private FileOutputStream fos = null;

	private InputStream is = null;
	private InputStreamReader isr = null;
	private BufferedReader br = null;

	private static DecimalFormat df = null;

	static {
		// 设置数字格式，保留一位有效小数
		df = new DecimalFormat("#0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);
	}

	/**
	 * 接收文件
	 *
	 * @param dis
	 * @throws IOException
	 */
	public void receiveFile(DataInputStream dis, Socket socket) throws IOException {
		try {
			// 文件名和长度
			String fileName = dis.readUTF();
			long fileLength = dis.readLong();
			// 目标机器上文件存放位置
			String targetPath = dis.readUTF();
			File dirTarget = new File(targetPath);
			if (!dirTarget.exists()) {
				dirTarget.mkdirs();
			}
			
			String md5FromSender = dis.readUTF();
			
			File file = new File(dirTarget.getAbsolutePath() + File.separatorChar + fileName);
			fos = new FileOutputStream(file);

			// 开始接收文件
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
				fos.write(bytes, 0, length);
				fos.flush();
			}
			LOGGER.info("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
			
			String md5 = FileUtil.getFileMd5(file);
			
			boolean md5Check = false;
			String msg = "";
			if (md5FromSender.equalsIgnoreCase(md5)) {
				msg = MessageFormat.format("文件【{0}】md5 校验成功", fileName);
				LOGGER.info(msg);

				md5Check = true;
			} else {
				msg = MessageFormat.format("文件【{0}】md5 校验失败", fileName);
				LOGGER.info(msg);
			}
			
			socket.shutdownInput();// 关闭输入流
			
			// 获取输出流，响应客户端的请求
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");// 解决客户端接收中文乱码问题
			pw = new PrintWriter(osw);
			HandleResultBean result = new HandleResultBean(md5Check ? 0 : 1, msg);
			pw.write(JSON.toJSONString(result));
			pw.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (fos != null) {
				fos.close();
			}
			
			if (pw != null) {
				pw.close();
			}
			
			if (osw != null) {
				osw.close();
			}
		}
	}
	
	/**
	 * 接收包含在目录中的文件
	 * 
	 * @param dis
	 * @throws IOException
	 */
	public void receiveFileInDir(DataInputStream dis, Socket socket) throws IOException {
		try {
			// 文件名和长度
			String fileName = dis.readUTF();
			long fileLength = dis.readLong();
			// 文件存放的相对目录
			String relativePath = dis.readUTF();
			// 文件存放的根目录
			String targetPath = dis.readUTF();
			File dirTarget = new File(targetPath);
			if (!dirTarget.exists()) {
				dirTarget.mkdirs();
			}
			
			String md5FromSender = dis.readUTF();

			relativePath = correctRelativePath(relativePath);

			String fileTargetDirPath = dirTarget.getAbsolutePath() + File.separatorChar + relativePath;
			File newDir = new File(fileTargetDirPath);
			if (!newDir.exists()) {
				newDir.mkdirs();// 如果不存在，则创建该目录
			}

			File file = new File(newDir.getAbsolutePath() + File.separatorChar + fileName);
			fos = new FileOutputStream(file);

			// 开始接收文件
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
				fos.write(bytes, 0, length);
				fos.flush();
			}
			LOGGER.info("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + getFormatFileSize(fileLength) + "] ========");
			
			String md5 = FileUtil.getFileMd5(file);

			boolean md5Check = false;
			String msg = "";
			if (md5FromSender.equalsIgnoreCase(md5)) {
				msg = MessageFormat.format("文件【{0}】md5 校验成功", fileName);
				LOGGER.info(msg);

				md5Check = true;
			} else {
				msg = MessageFormat.format("文件【{0}】md5 校验失败", fileName);
				LOGGER.info(msg);
			}

			socket.shutdownInput();// 关闭输入流

			// 获取输出流，响应客户端的请求
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");// 解决客户端接收中文乱码问题
			pw = new PrintWriter(osw);
			HandleResultBean result = new HandleResultBean(md5Check ? 0 : 1, msg);
			pw.write(JSON.toJSONString(result));
			pw.flush();
		} catch (IOException e) {
			throw e;
		}finally {
			if (fos != null) {
				fos.close();
			}
			
			if (pw != null) {
				pw.close();
			}
			
			if (osw != null) {
				osw.close();
			}			
			
		}
		
	}

	/**
	 * 创建目录
	 * 
	 * @param dis
	 * @param socket
	 * @throws IOException
	 */
	public void createDir(DataInputStream dis, Socket socket) throws IOException {
		try {
			// 相对路径
			String relativePath = dis.readUTF();
			// 目标机器上文件存放位置
			String targetPath = dis.readUTF();
			File directory = new File(targetPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			relativePath = correctRelativePath(relativePath);

			String pathName = directory.getAbsolutePath() + File.separatorChar + relativePath;
			File newDir = new File(pathName);
			if (!newDir.exists()) {
				newDir.mkdirs();// 如果不存在，则创建该目录
			}

			// 告知客户端，目录创建成功
			socket.shutdownInput();// 关闭输入流

			// 获取输出流，响应客户端的请求
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");// 解决客户端接收中文乱码问题
			pw = new PrintWriter(osw);
			pw.write(MessageFormat.format("目录{0}创建成功", pathName));
			pw.flush();
		} catch (IOException e) {
			throw e;
		}finally {
			if (pw != null) {
				pw.close();
			}
			
			if (osw != null) {
				osw.close();
			}			
			
		}
		
	}

	/**
	 * 执行命令
	 * 
	 * @param dis
	 * @param socket
	 * @throws IOException
	 */
	public void execShell(DataInputStream dis, Socket socket) throws IOException {
		try {
			// 需要执行的命令
			String shell = dis.readUTF();
			String charsetName;

			String os = System.getProperty("os.name");
			// 简单判断，实际上并不只有这两种操作系统
			if (os.toLowerCase().startsWith("win")) {
				LOGGER.info("这个是windows系统");
				shell = MessageFormat.format("cmd /c \"{0}\"", shell);
				charsetName = "gbk";
			} else {
				LOGGER.info("这个是linux系统");
				shell = MessageFormat.format("/bin/sh -c \"{0}\"", shell);
				charsetName = "utf-8";
			}

			boolean execResult = false;
			String msg = "";
			Process process;
			try {
				Runtime runtime = Runtime.getRuntime();

				LOGGER.info("开始执行。。。");
				process = runtime.exec(shell);

				// 打印执行的输出结果
				is = process.getInputStream();
				isr = new InputStreamReader(is, charsetName); // windows下用gbk：解决输出乱码；linux下改用utf-8
				br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					LOGGER.info(line);
				}
				execResult = true;
			} catch (Exception e) {
				LOGGER.error("FileTransferReceiver.execShell 捕获异常：", e);
				msg=e.getMessage();
				execResult = false;
			}

			// 告知客户端，目录创建成功
			socket.shutdownInput();// 关闭输入流

			// 获取输出流，响应客户端的请求
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");// 解决客户端接收中文乱码问题
			pw = new PrintWriter(osw);
			HandleResultBean result = new HandleResultBean(execResult ? 0 : 1, msg);
			//pw.write(execResult ? "远程命令执行成功" : "远程命令执行失败");
			pw.write(JSON.toJSONString(result));
			pw.flush();
		} catch (Exception e) {
			throw e;
		}finally {
			if (pw != null) {
				pw.close();
			}

			if (osw != null) {
				osw.close();
			}

			if (is != null) {
				is.close();
			}

			if (isr != null) {
				isr.close();
			}			

			if (br != null) {
				br.close();
			}
		}
		
	}

	/**
	 * 格式化文件大小
	 *
	 * @param length
	 * @return
	 */
	private String getFormatFileSize(long length) {
		double size = ((double) length) / (1 << 30);
		if (size >= 1) {
			return df.format(size) + "GB";
		}
		size = ((double) length) / (1 << 20);
		if (size >= 1) {
			return df.format(size) + "MB";
		}
		size = ((double) length) / (1 << 10);
		if (size >= 1) {
			return df.format(size) + "KB";
		}
		return length + "B";
	}

	/**
	 * 调整客户端传来的相对目录中的路径分隔符
	 * 
	 * @param relativePath
	 * @return
	 */
	private String correctRelativePath(String relativePath) {
		String os = System.getProperty("os.name");
		// 简单判断，实际上并不只有这两种操作系统
		if (os.toLowerCase().startsWith("win")) {
			LOGGER.info("这个是windows系统");
			LOGGER.info("发送端传过来的相对目录是：" + relativePath);
			if (!relativePath.equals("") && relativePath.contains("/")) {
				relativePath = relativePath.replaceAll("/", "\\\\");
			}
		} else {
			LOGGER.info("这个是linux系统");//除了windows外，暂时都当成是 linux吧
			LOGGER.info("发送端传过来的相对目录是：" + relativePath);
			if (!relativePath.equals("") && relativePath.contains("\\")) {
				relativePath = relativePath.replaceAll("\\\\", "/");
			}
		}

		return relativePath;
	}
}
