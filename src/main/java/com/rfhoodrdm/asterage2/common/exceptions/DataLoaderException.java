/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.common.exceptions;

/**
 * Exception to be thrown if the data loader encounters an inability to load a file it needs.
 * The message contains the file name.
 */
public class DataLoaderException
extends Exception
{
	//Defining the three types of constructors for full exception compatibility.
	public DataLoaderException ( String message )
	{
		super(message);
	} //end constructor with string argument.
	
	public DataLoaderException ( String message, Throwable throwable )
	{
		super( message, throwable);
	} //end constructor with string and throwable argument.
	
	public DataLoaderException ( Throwable throwable )
	{
		super ( throwable );
	} //end constructor with throwable argument
} //end DataLoderException 
