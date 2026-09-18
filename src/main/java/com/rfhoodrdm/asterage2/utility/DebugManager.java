package com.rfhoodrdm.asterage2.utility;

import com.rfhoodrdm.asterage2.constants.GameConstants;

/*		ERROR MESSAGE SCALE: 	(meaning)
		1 - FATAL				unrecoverable error has occurred. Shut down and restart.
		2 - ERROR				an error that will impact the process of an entire component has occurred.
		3 - SEVERE				an error more serious than 3 has occurred, but has not yet compromised the entire component.
		4 - WARNING				unexpected occurrence. Logical error indicated, but won't impact workings of the program.
		5 - DEBUG				message shows inner workings of components as they are being called
		6 - TRACE				shows even more inner workings than 5. Message contents are displayed.
*/

/**
 * class DebugManager takes debugging messages and decides what to do with them.
 * Methods are static, so that debug handler can be accessed from any point in the program.
 * 
 * We utilize the debug level in GameConstants to decide what we want to do with our information.
 * There are several numerical levels of debug information, with 6 (the current highest) being most verbose,
 * and 1 showing only the most critical of errors.
 */
public class DebugManager
{
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
    public static void logMessage ( int severity ,String theMessage )
    {
	//If the debugging level warrants showing the message, then print it to the
	//standard error stream.
	if ( severity <= GameConstants.DEBUGGING_LEVEL )
	{
	    displayMessagePrologue ( severity );
	    displayMessage ( theMessage );
	} //end if clause to determine if message should be logged
    } //end function logMessage

	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
    /**
     * Display the type of message being logged. Chosen based on severity passed in.
     * @param severity Integer from 1 to 6, with 1 being most severe and 6 being least.
     */
    private static void displayMessagePrologue ( int severity )
    {
		String prologue = "";
		//Select the label to output with the message.
		switch ( severity )
		{
			case 1: 
			prologue = "[ FATAL ] "; 
			break;
			case 2: 
			prologue = "[ ERROR ] "; 
			break;
			case 3: 
			prologue = "[ SEVERE] "; 
			break;
			case 4: 
			prologue = "[WARNING] "; 
			break;
			case 5: 
			prologue = "[ DEBUG ] "; 
			break;
			case 6: 
			prologue = "[ TRACE ] "; 
			break;
		} //end switch to select the message prologue.
		
		System.err.print( prologue );
    } //end function displayMessage Prologue
    
    /**
     * Log the actual message.
     * @param theMessage The string form of the message to be logged.
     */
    private static void displayMessage ( String theMessage )
    {
	//use standard error stream. We can redirect this later if we want to save to a file.
	System.err.println( theMessage );
    } //end function displayMessagE

} //end class DebugManager definition