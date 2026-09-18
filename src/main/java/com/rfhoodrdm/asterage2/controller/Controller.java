package com.rfhoodrdm.asterage2.controller;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.state.State;
import com.rfhoodrdm.asterage2.utility.DebugManager;

/**
 * Class controller is the top level game logic implementation class.
 * It in turn invokes the game logic to update the game state for the splash screen, and either AsteRAGE 1 or AsteRAGE 2
 */
public class Controller
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    private State state;
    private GUI gui;
	
    private TitleController titleController;
    private Asterage1Controller asterage1Controller;
    private Asterage2Controller asterage2Controller;
	
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Controller() {
		titleController = new TitleController();
		asterage1Controller = new Asterage1Controller();
		asterage2Controller = new Asterage2Controller();
		
		//set the master controller reference right away
		titleController.setController ( this ) ;
		asterage1Controller.setController( this );
		asterage2Controller.setController( this );
	} 
	
	public void setState ( State passedState ) {
		this.state = passedState;
		
		//once we have the master state object, set all of our components' state references
		titleController.setTitleState ( state.getTitleState() );	
		asterage1Controller.setAsterage1State( state.getAsterage1State() );
		asterage2Controller.setAsterage2State( state.getAsterage2State() );
	} 
	
	public void setGUI ( GUI passedGUI ) {
		this.gui = passedGUI;
		
		//pass each controller subcomponent a reference to the high level gui object.
		titleController.setGui(passedGUI);
		asterage1Controller.setGui ( gui );
		asterage2Controller.setGUI( gui );
	} 
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	public void showInitialState () {
		//show the initial screen.
		gui.showInitialGUI();	
	} 
	
	/**
	 * This method invokes the state update logic for the corresponding active game state.
	 */
	public void updateState () {
		CurrentState whatState = state.getCurrentState();
		
		//update the currently active state.
		switch ( whatState ) {
			case TITLE_SCREEN:
				titleController.updateState();
				break;
				
			case ASTERAGE_1:
				asterage1Controller.updateAsterage1State();
				break;
			
			case ASTERAGE_2:
				asterage2Controller.updateState();
				break;
		} 
	} 
	
	/**
	 * Organizes the switch of the current, officially active state between Title screen, Asterage 1, and Asterage 2.
	 */
	public void switchActiveState ( CurrentState newState ) {
		//change and reinitialize the current active state.
		state.changeCurrentActiveState( newState );
		
		//initialize the respective games.
		asterage2Controller.beginNewGame();
		
		//show the correct gui components.
		gui.changeCurrentGuiShown();
	} 
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Receives key press and release events from the keyboard input module, and decides
	 * how to process them, according to which is the active state. Only the active state should be
	 * receiving keyboard input.
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent )
	{
		//get the current active controller, by fetching which state is currently active.
		//we need to deliver the key event to that controller.
		CurrentState currentActiveState = state.getCurrentState();
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
				
		} 
	} 
} 
