package com.filestransfer.protocol;

public class RemoteExecShellPacket extends Packet {
	private String shell;

	public RemoteExecShellPacket() {
	}

	public RemoteExecShellPacket(String protocolHeader, String execType, String shell) {
		super(protocolHeader, execType);
		this.shell = shell;
	}

	public String getShell() {
		return shell;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}

}
