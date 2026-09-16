package com.rfhoodrdm.asterage2.controller;

import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.state.State;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.GameConstants;
import java.awt.event.KeyEvent;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 * Class controller is the top level game logic implementation class.
 * It in turn invokes the game logic to update the game state for the splash screen, and either AsteRAGE 1 or AsteRAGE 2
 */
public class Controller
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    State state;
	GUI gui;
	
	TitleController titleController;
	Asterage1Controller asterage1Controller;
	Asterage2Controller asterage2Controller;
	
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Controller()
	{
		titleController = new TitleController();
		asterage1Controller = new Asterage1Controller();
		asterage2Controller = new Asterage2Controller();
		
		//set the master controller reference right away
		titleController.setController ( this ) ;
		asterage1Controller.setController( this );
		asterage2Controller.setController( this );
	} //end constructor
	
	public void setState ( State passedState )
	{
		this.state = passedState;
		
		//once we have the master state object, set all of our components' state references
		titleController.setTitleState ( state.getTitleStateObject() );	
		asterage1Controller.setAsterage1State( state.getAsterage1StateObject() );
		asterage2Controller.setAsterage2State( state.getAsterage2StateObject() );
	} //end method setState
	
	public void setGUI ( GUI passedGUI )
	{
		this.gui = passedGUI;
		
		//pass each controller subcomponent a reference to the high level gui object.
		titleController.setGUI ( gui );
		asterage1Controller.setGUI ( gui );
		asterage2Controller.setGUI( gui );
	} //end function setGUI
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	public void showInitialState ()
	{
		//show the initial screen.
		gui.showInitialGUI();	
	} 
	
	/**
	 * This method invokes the state update logic for the corresponding active game state.
	 */
	public void updateState ()
	{
		GameConstants.CURRENT_STATE whatState = state.getCurrentActiveState();
		
		//DebugManager.logMessage(6 , "Updating game state: " + whatState.toString() );
		
		//update the currently active state.
		switch ( whatState )
		{
			case TITLE_SCREEN:
				titleController.updateState();
				break;
				
			case ASTERAGE_1:
				asterage1Controller.updateAsterage1State();
				break;
			
			case ASTERAGE_2:
				asterage2Controller.updateState();
				break;
		} //end switch
		
	} //end function updateState
	
	/**
	 * Organizes the switcg of the current, officially active state between Title screen, Asterage 1, and Asterage 2.
	 * @param newState 
	 */
	public void switchActiveState ( GameConstants.CURRENT_STATE newState )
	{
		//change and reinitialize the current active state.
		state.changeCurrentActiveState( newState );
		
		//initialize the respective games.
		asterage2Controller.beginNewGame();
		
		//show the correct gui components.
		gui.changeCurrentGuiShown();
	} //end function switchActiveState
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Receives key press events from the gui.
	 * @param keyDown The character corresponding to the key pressed.
	 */
	
	/**
	 * Receives key press and release events from the keyboard input module, and decides
	 * how to process them, according to which is the active state. Only the active state should be
	 * receiving keyboard input.
	 * @param key The key that corresponds to the one pressed on the keyboard.
	 * @param whichEvent UP or DOWN, pertaining to releases and presses, respectively.
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent )
	{
		//get the current active controller, by fetching which state is currently active.
		//we need to deliver the key event to that controller.
		GameConstants.CURRENT_STATE currentActiveState = state.getCurrentActiveState();
		switch ( currentActiveState )
		{
			case TITLE_SCREEN:
				titleController.processKeyEvent ( keyCode, whichEvent );
				break;
				
			case ASTERAGE_1:
				asterage1Controller.processKeyEvent ( keyCode, whichEvent );
				break;
				
			case ASTERAGE_2:
				asterage2Controller.processKeyEvent ( keyCode, whichEvent );
				break;
			
			default:
				//we shouldn't get here. If so, then we are very much in error.
				DebugManager.logMessage(2, "Received keyboard input designated for an unknown game controller.");
				
		} //end switch based on currently active state.
		
	} //end function processKeyDown
} //end class controller definition
