package com.rfhoodrdm.asterage2.gui.input;

import com.rfhoodrdm.asterage2.controller.Controller;
import java.awt.event.KeyEvent;
import com.rfhoodrdm.asterage2.utility.DebugManager;

/**
 *	This class is responsible for gathering key strokes and key releases, and deliver them to the 
 * master controller class. The GameKeyAdapter is intended to be attached to the top level
 * game frame in the gui.
 */
public class GameKeyAdapter
extends java.awt.event.KeyAdapter
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	Controller controller;
	
	public GameKeyAdapter ( )
	{
		
	} //end constructor
	
	public void setController ( Controller passedController )
	{
		this.controller = passedController;
	} //end function setController
	
	@Override
	public void keyPressed(KeyEvent e)
	{
	   //Find out which key it was and pass the key press forward.
	   int pressedKey = e.getKeyCode(); 
	   DebugManager.logMessage(6, "Keypress registered: " + pressedKey);
	   controller.processKeyEvent( pressedKey, GAME_KEY_EVENT.DOWN );
	    
	} //end function keyPressed

	@Override
	public void keyReleased(KeyEvent e)
	{
	    //find out which key it was and pass the key release forward.
	    int releasedKey = e.getKeyCode();
		//TODO convert keys to lower case, to avoid caps lock bug.
		DebugManager.logMessage(6, "Keyrelease registered: " + releasedKey);
	    controller.processKeyEvent( releasedKey, GAME_KEY_EVENT.UP );
	} //end function keyReleased.
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	/**
	 * Used to distinguish what types of key events are being delivered to the controller.
	 */
	public static enum GAME_KEY_EVENT
	{
		UP,
		DOWN;
	} //end enum GAME_KEY_EVENT definition
} //end class GameKeyAdapter
