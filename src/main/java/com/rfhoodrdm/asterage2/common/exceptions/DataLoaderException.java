package com.rfhoodrdm.asterage2.common.exceptions;

/**
 * Exception to be thrown if the data loader encounters an inability to load a file it needs.
 * The message contains the file name.
 */
public class DataLoaderException extends Exception
{
	private static final long serialVersionUID = 7228441195629271731L;

	public DataLoaderException() {
		super();
	}

	public DataLoaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataLoaderException(String message) {
		super(message);
	}

	public DataLoaderException(Throwable cause) {
		super(cause);
	}
} 
