package com.filestransfer.protocol;

public class SendFilePacket extends Packet {
	private String fileName;
	private long fileLength;
	private String fileMd5;
	private String targetPath;

	public SendFilePacket() {
	}

	public SendFilePacket(String protocolHeader, String execType, String fileName, long fileLength, String fileMd5, String targetPath) {
		super(protocolHeader, execType);
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.fileMd5 = fileMd5;
		this.targetPath = targetPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

}
