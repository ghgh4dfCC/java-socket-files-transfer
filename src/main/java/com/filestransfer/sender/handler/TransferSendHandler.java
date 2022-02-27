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
import com.filestransfer.beans.ExecType;
import com.filestransfer.beans.HandleResultBean;
import com.filestransfer.utils.ExceptionUtil;
import com.filestransfer.utils.FileUtil;

public class TransferSendHandler {
	private static final Logger logger = Logger.getLogger(TransferSendHandler.class);
	
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
	public void sendFile(String filePath, String targetPath) throws IOException {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				fis = new FileInputStream(file);
				dos = new DataOutputStream(client.getOutputStream());

				// 执行动作
				dos.writeUTF(ExecType.SINGLE_FILE);
				dos.flush();
				// 文件名和长度
				dos.writeUTF(file.getName());
				dos.flush();
				dos.writeLong(file.length());
				dos.flush();
				// 目标机器上文件存放位置
				dos.writeUTF(targetPath);
				dos.flush();
				// 文件的md5码
				String md5 = FileUtil.getFileMd5(file);
				dos.writeUTF(md5);
				dos.flush();

				// 开始传输文件
				logger.info("======== 开始传输文件 ========");
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress += length;
					logger.info("| " + (100 * progress / file.length()) + "% |");
				}
				logger.info("======== 文件传输成功 ========");

				client.shutdownOutput();// 关闭输出流

				// 获取输入流，接收服务器端响应信息
				is = client.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String data = null;
				while ((data = br.readLine()) != null) {
					logger.info(data);
					HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
					//code为0表示正常，非0 表示有异常
					if (0 != result.getCode()) {
						ExceptionUtil.throwException(result.getMsg());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e.fillInStackTrace());
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
	public void createDir(String relativePath, String targetPath) throws IOException {
		try {
			dos = new DataOutputStream(client.getOutputStream());

			// 执行动作
			dos.writeUTF(ExecType.CREATE_DIR);
			dos.flush();
			// 相对目录
			dos.writeUTF(relativePath);
			dos.flush();

			// 目标机器上文件存放位置
			dos.writeUTF(targetPath);
			dos.flush();

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				logger.info(data);
			}
		} catch (IOException e) {
			logger.error(e, e.fillInStackTrace());
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
	public void sendFileInDir(String filePath, String relativePath, String targetPath) throws Exception {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				fis = new FileInputStream(file);
				dos = new DataOutputStream(client.getOutputStream());

				// 执行动作
				dos.writeUTF(ExecType.FILE_IN_DIR);
				dos.flush();
				// 文件名和长度
				dos.writeUTF(file.getName());
				dos.flush();
				dos.writeLong(file.length());
				dos.flush();
				// 目标机器上文件存放的相对目录
				dos.writeUTF(relativePath);
				dos.flush();
				// 目标机器上文件存放的根目录
				dos.writeUTF(targetPath);
				dos.flush();				
				// 文件的md5码
				String md5 = FileUtil.getFileMd5(file);
				dos.writeUTF(md5);
				dos.flush();

				// 开始传输文件
				logger.info("======== 开始传输文件 ========");
				byte[] bytes = new byte[1024];
				int length = 0;
				long progress = 0;
				while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
					dos.write(bytes, 0, length);
					dos.flush();
					progress += length;
					logger.info("| " + (100 * progress / file.length()) + "% |");
				}
				logger.info("======== 文件传输成功 ========");
				
				client.shutdownOutput();// 关闭输出流

				// 获取输入流，接收服务器端响应信息
				is = client.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String data = null;
				while ((data = br.readLine()) != null) {
					logger.info(data);
					HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
					//code为0表示正常，非0 表示有异常
					if (0 != result.getCode()) {
						ExceptionUtil.throwException(result.getMsg());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e, e.fillInStackTrace());
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
	public void remoteExecShell(String shell) throws Exception {
		try {
			dos = new DataOutputStream(client.getOutputStream());

			// 执行动作
			dos.writeUTF(ExecType.REMOTE_EXEC_SHELL);
			dos.flush();
			// 需要执行的命令
			dos.writeUTF(shell);
			dos.flush();

			client.shutdownOutput();// 关闭输出流

			// 获取输入流，接收服务器端响应信息
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String data = null;
			while ((data = br.readLine()) != null) {
				logger.info(data);
				HandleResultBean result = JSON.parseObject(data, HandleResultBean.class);
				//code为0表示正常，非0 表示有异常
				if (0 != result.getCode()) {
					ExceptionUtil.throwException(result.getMsg());
				}
			}
		} catch (Exception e) {
			logger.error(e, e.fillInStackTrace());
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
