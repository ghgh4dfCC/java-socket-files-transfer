package com.filestransfer.sender.handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.filestransfer.bean.ExecType;
import com.filestransfer.bean.HandleResultBean;
import com.filestransfer.protocol.CreateDirPacket;
import com.filestransfer.protocol.RemoteExecShellPacket;
import com.filestransfer.protocol.SendFileInDirPacket;
import com.filestransfer.protocol.SendFilePacket;
import com.filestransfer.util.ExceptionUtil;
import com.filestransfer.util.FileUtil;

public class TransferSendHandler {
	private static final Logger LOGGER = Logger.getLogger(TransferSendHandler.class);
	
	private Socket client;
	private FileInputStream fis;
	private DataOutputStream dos;
	private InputStream is;
	private BufferedReader br;
	
	public TransferSendHandler(Socket client) {
		this.client=client;
	}
	
	/**
	 * 向接收端传输文件
	 * @throws IOException 
	 *
	 */
	public void sendFile(File fileInSender, SendFilePacket packet) throws IOException {
		try {
			fis = new FileInputStream(fileInSender);
			dos = new DataOutputStream(client.getOutputStream());

			// 协议头
			dos.writeUTF(packet.getProtocolHeader());
			dos.flush();
			// 执行动作
			dos.writeUTF(packet.getExecType());
			dos.flush();
			// 文件名和长度
			dos.writeUTF(packet.getFileName());
			dos.flush();
			dos.writeLong(packet.getFileLength());
			dos.flush();
			// 目标机器上文件存放位置
			dos.writeUTF(packet.getTargetPath());
			dos.flush();
			// 文件的md5码
			// String md5 = FileUtil.getFileMd5(file);
			dos.writeUTF(packet.getFileMd5());
			dos.flush();

			// 开始传输文件
			LOGGER.info("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;
			long progress = 0;
			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
				progress += length;
				LOGGER.info("| " + (100 * progress / fileInSender.length()) + "% |");
			}
			LOGGER.info("======== 文件传输成功 ========");

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				LOGGER.info(data);
				HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
				// code为0表示正常，非0 表示有异常
				if (0 != result.getCode()) {
					ExceptionUtil.throwException(result.getMsg());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e, e.fillInStackTrace());
			throw e;
		} finally {
			if (fis != null) {
				fis.close();
			}

			if (dos != null) {
				dos.close();
			}

			if (is != null) {
				is.close();
			}
			if (br != null) {
				br.close();
			}

			client.close();
		}
	}
	
	/**
	 * 向接收端创建目录
	 *
	 * @throws Exception
	 */
	public void createDir(CreateDirPacket packet) throws IOException {
		try {
			dos = new DataOutputStream(client.getOutputStream());

			// 协议头
			dos.writeUTF(packet.getProtocolHeader());
			dos.flush();
			// 执行动作
			dos.writeUTF(packet.getExecType());
			dos.flush();

			// 相对目录
			dos.writeUTF(packet.getRelativePath());
			dos.flush();

			// 目标机器上文件存放位置
			dos.writeUTF(packet.getTargetPath());
			dos.flush();

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				LOGGER.info(data);
			}
		} catch (IOException e) {
			LOGGER.error(e, e.fillInStackTrace());
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (is != null) {
				is.close();
			}
			if (br != null) {
				br.close();
			}

			client.close();
		}
	}
	
	/**
	 * 向接收端传输文件/传输整个目录
	 *
	 * @throws Exception
	 */
	public void sendFileInDir(File fileInSender, SendFileInDirPacket packet) throws Exception {
		try {
			fis = new FileInputStream(fileInSender);
			dos = new DataOutputStream(client.getOutputStream());

			// 协议头
			dos.writeUTF(packet.getProtocolHeader());
			dos.flush();
			// 执行动作
			dos.writeUTF(packet.getExecType());
			dos.flush();

			// 文件名和长度
			dos.writeUTF(packet.getFileName());
			dos.flush();
			dos.writeLong(packet.getFileLength());
			dos.flush();
			// 目标机器上文件存放的相对目录
			dos.writeUTF(packet.getRelativePath());
			dos.flush();
			// 目标机器上文件存放的根目录
			dos.writeUTF(packet.getTargetPath());
			dos.flush();
			// 文件的md5码
			// String md5 = FileUtil.getFileMd5(file);
			dos.writeUTF(packet.getFileMd5());
			dos.flush();

			// 开始传输文件
			LOGGER.info("======== 开始传输文件 ========");
			byte[] bytes = new byte[1024];
			int length = 0;
			long progress = 0;
			while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
				dos.write(bytes, 0, length);
				dos.flush();
				progress += length;
				LOGGER.info("| " + (100 * progress / fileInSender.length()) + "% |");
			}
			LOGGER.info("======== 文件传输成功 ========");

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				LOGGER.info(data);
				HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
				// code为0表示正常，非0 表示有异常
				if (0 != result.getCode()) {
					ExceptionUtil.throwException(result.getMsg());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e, e.fillInStackTrace());
		} finally {
			if (fis != null) {
				fis.close();
			}

			if (dos != null) {
				dos.close();
			}

			if (is != null) {
				is.close();
			}
			if (br != null) {
				br.close();
			}
			client.close();
		}
	}
	
	/**
	 * 远程执行命令
	 * 
	 * @param shell
	 * @throws Exception
	 */
	public void remoteExecShell(RemoteExecShellPacket packet) throws Exception {
		try {
			dos = new DataOutputStream(client.getOutputStream());

			// 协议头
			dos.writeUTF(packet.getProtocolHeader());
			dos.flush();
			// 执行动作
			dos.writeUTF(packet.getExecType());
			dos.flush();

			// 需要执行的命令
			dos.writeUTF(packet.getShell());
			dos.flush();

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				LOGGER.info(data);
				HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
				// code为0表示正常，非0 表示有异常
				if (0 != result.getCode()) {
					ExceptionUtil.throwException(result.getMsg());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e, e.fillInStackTrace());
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (is != null) {
				is.close();
			}
			if (br != null) {
				br.close();
			}

			client.close();
		}
	}
}
