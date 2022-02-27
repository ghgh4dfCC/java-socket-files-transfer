package com.filestransfer.exception;

public class FilesTransferException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7136151389641487431L;

	public FilesTransferException(String message) {
		super(message);
	}

	public FilesTransferException(Exception e) {
		super(e);
	}

	public FilesTransferException(String message, Throwable cause) {
		super(message, cause);
	}
}
