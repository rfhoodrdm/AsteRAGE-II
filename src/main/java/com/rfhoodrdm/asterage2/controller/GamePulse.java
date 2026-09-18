package com.rfhoodrdm.asterage2.controller;

import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.utility.DebugManager;

/**
 * The GamePulse class is responsible for clock ticks that trigger game state
 * changes, as well as screen redraws. It is its own thread.
 */
public class GamePulse
extends Thread
{
    /*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    private int restBetweenTicks;		//how much time between clock pulses.
    
	//references to other components.
    private Controller controller;
    private GUI gui;
    
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
    public GamePulse()
    {
		//Get the number of frames per second from the GameConstants object.
		restBetweenTicks = 1000 / GameConstants.FRAMES_PER_SECOND;
    }
    
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

    public void setController ( Controller passedController ) {
		this.controller = passedController;
    } 
    
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
 
    /**
     * Entry point of the thread. Enter into an infinite loop, sleeping for a designated
     * amount of time, then awaking and triggering a state update and screen redraw.
     */
    public void run()
    {
		//now, after all is prepared, do the initial show of the gui.
		//This is to prevent null pointer exceptions in painting, if we are relying on state to decide how to display things.
		controller.showInitialState();
		
		while ( true ) {
			//must use a try-catch block to handle possible interrupted exceptions.
			try
			{
				//First, sleep a while.
				Thread.sleep ( restBetweenTicks );
			}
			catch ( InterruptedException e) 
			{
				//nothing to do.
			}

			controller.updateState();

		} 
    }
}
