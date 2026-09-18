package com.rfhoodrdm.asterage2.entry;

import com.rfhoodrdm.asterage2.state.State;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.controller.GamePulse;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;

/**
 * AsteRAGE 2 entry class.
 * Prepares all of the other classes and components as needed.
 */
public class AsteRAGE2
{
	/**
	 * Entry point for plain java.
	 * TODO: remove this.
	 */
//	public static void main (String [] args)
//	{
//		initializeGame();
//	} 
	
	public static void initializeGame() {
		//load the assets.
		DataLoader dataLoader = new DataLoader();
		dataLoader.startLoading();
		
		//Begin loading and initializing the main components of AsteRAGE 2.
		//Pass data loader to modules which need to reference retrieved information.
		State state = new State( dataLoader );		
	
		Controller controller = new Controller();
		GUI gui = new GUI();
		GamePulse gamePulse = new GamePulse();
		
		//Set references to other components from here.
		controller.setGUI ( gui );
		controller.setState ( state );
		
		gui.setController( controller );
		gui.setState( state );
		
		gamePulse.setController( controller );
		
		//set the threads to running. Let the game begin!
		gamePulse.start();
	}
} 
