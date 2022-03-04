package com.filestransfer.protocol;

public class CreateDirPacket extends Packet {
	private String relativePath;
	private String targetPath;

	public CreateDirPacket() {
	}

	public CreateDirPacket(String protocolHeader, String execType, String relativePath, String targetPath) {
		super(protocolHeader, execType);
		this.relativePath = relativePath;
		this.targetPath = targetPath;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

}
