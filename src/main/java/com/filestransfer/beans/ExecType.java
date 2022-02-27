package com.filestransfer.beans;

/**
 *
 * 执行/操作类型，目前有传输单文件、传输整个目录、远程执行命令
 */
public interface ExecType {

	/**
	 * 传输单文件
	 */
	String SINGLE_FILE = "SINGLE_FILE";
	/**
	 * 创建目录
	 */
	String CREATE_DIR = "CREATE_DIR";
	/**
	 * 传输整个目录中的文件
	 */
	String FILE_IN_DIR = "FILE_IN_DIR";
	/**
	 * 远程执行命令
	 */
	String REMOTE_EXEC_SHELL = "REMOTE_EXEC_SHELL";
}
