package com.rfhoodrdm.asterage2.state;

import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.GameConstants;

import java.util.concurrent.Semaphore;

/**
 * State is the top level class in the game's "model" which holds the game state.
 * Holds information pertaining to the splash screen, as well as AsteRAGE 1 and 2.
 */
public class State
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	//references to other components.
	GUI gui;		
	TitleState titleState;
	Asterage1State asterage1State;
	Asterage2State asterage2State;
	
	//state semaphore lock, protecting against concurrent access.
	Semaphore stateLock;
	
	//Which state is active? Default is title screen.
	GameConstants.CURRENT_STATE current_state;
	
	public void setGUI ( GUI passedGUI )
	{
		this.gui = passedGUI;
	} //end function setGUI
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public State ( DataLoader passedLoader )
	{
		//initialize the state semaphore lock to 1 permit, fairness enforced.
		stateLock = new Semaphore ( 1, true );
		
		//the initial currently active state is the title screen.
		 this.current_state = GameConstants.CURRENT_STATE.TITLE_SCREEN;
		 
		 //initialize all state objects.
		 titleState = new TitleState();
		 titleState.initializeTitleState();
		 
		 asterage1State = new Asterage1State( passedLoader );
		 asterage1State.initializeAsterage1State();
		 asterage2State = new Asterage2State( passedLoader );
		 asterage2State.initializeAsterage2State();
	} //end constructor
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * This method returns which state is currently active as far as updates go.
	 * @return GameConstants.CURRENT_STATE enumerated constant representing the current, active state.
	 */
	public GameConstants.CURRENT_STATE getCurrentActiveState ()
	{	
		return this.current_state;	
	} //end function getCurrentActiveState
	
	/**
	 * Changes the currently active state to a new one.
	 * @param newCurrentState 
	 */
	public void changeCurrentActiveState( GameConstants.CURRENT_STATE newCurrentState )
	{
		//acquire the state lock to make changes.
		stateLock.acquireUninterruptibly();
		
		DebugManager.logMessage(5, "Changing current active state to: " + newCurrentState.toString() );
		this.current_state = newCurrentState;
		
		//also invoke that state's initialization function, to return its values to default -- we're starting anew.
		titleState.initializeTitleState();
		asterage1State.initializeAsterage1State();
		asterage2State.initializeAsterage2State();
		
		//release the lock.
		stateLock.release();
	} //end function changeCurrentActiveState
	
	public TitleState getTitleStateObject()
	{
		return this.titleState;
	} //end function getTitleStateObject
	
	public Asterage1State getAsterage1StateObject()
	{
		return this.asterage1State;
	} //end function getAsterage1StateObject
	
	public Asterage2State getAsterage2StateObject()
	{
		return this.asterage2State;
	} 
	
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
