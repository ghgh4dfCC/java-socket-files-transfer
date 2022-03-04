package com.filestransfer.protocol;

public class Packet {
	private String protocolHeader;
	private String execType;

	public Packet() {
	}

	public Packet(String protocolHeader, String execType) {
		this.protocolHeader = protocolHeader;
		this.execType = execType;
	}

	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public String getProtocolHeader() {
		return protocolHeader;
	}

	public void setProtocolHeader(String protocolHeader) {
		this.protocolHeader = protocolHeader;
	}

}
