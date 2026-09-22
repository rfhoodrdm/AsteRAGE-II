package com.rfhoodrdm.asterage2.controller;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.GUI;

import lombok.RequiredArgsConstructor;

/**
 * The GamePulse class is responsible for clock ticks that trigger game state
 * changes, as well as screen redraws. 
 */
@Component
@RequiredArgsConstructor
public class GamePulse
	implements Runnable {
	
    /*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
    private int restBetweenTicks = 1000 / GameConstants.FRAMES_PER_SECOND;		//how much time between clock pulses.
    
	//references to other components.
    private final Controller controller;
    private final GUI gui;
    
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
    
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
    
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
 
    /**
     * Entry point of the thread. Enter into an infinite loop, sleeping for a designated
     * amount of time, then awaking and triggering a state update and screen redraw.
     */
    @Override
    public void run() {
		
		while ( true ) {
			try	{
				Thread.sleep ( restBetweenTicks );
			} catch ( InterruptedException e) {
				Thread.currentThread().interrupt();
                return;
			}

			controller.updateState();
			gui.repaint();
		} 
    }
}
