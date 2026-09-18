package com.rfhoodrdm.asterage2.state;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;

import lombok.Getter;
import lombok.Setter;

/**
 * State is the top level class in the game's "model" which holds the game state.
 * Holds information pertaining to the splash screen, as well as AsteRAGE 1 and 2.
 */



@Slf4j
public class State
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	//references to other components.		
	@Getter
	private TitleState titleState;
	
	@Getter
	private Asterage1State asterage1State;
	
	@Getter
	private Asterage2State asterage2State;
	
	//state semaphore lock, protecting against concurrent access.
	private Semaphore stateLock;
	

	@Getter
	private CurrentState currentState;		//Which game state is the active one? Default is title screen.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public State ( DataLoader passedLoader )
	{
		//initialize the state semaphore lock to 1 permit, fairness enforced.
		stateLock = new Semaphore ( 1, true );
		
		//the initial currently active state is the title screen.
		 this.currentState = CurrentState.TITLE_SCREEN;
		 
		 //initialize all state objects.
		 titleState = new TitleState();
		 titleState.initializeTitleState();
		 
		 asterage1State = new Asterage1State( passedLoader );
		 asterage1State.initializeAsterage1State();
		 asterage2State = new Asterage2State( passedLoader );
		 asterage2State.initializeAsterage2State();
	} 
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	/**
	 * Changes the currently active state to a new one.
	 * @param newCurrentState 
	 */
	public void changeCurrentActiveState( CurrentState newCurrentState )
	{
		//acquire the state lock to make changes.
		stateLock.acquireUninterruptibly();
		
		log.debug("Changing current active state to: {}", newCurrentState);
		this.currentState = newCurrentState;
		
		//also invoke that state's initialization function, to return its values to default -- we're starting anew.
		titleState.initializeTitleState();
		asterage1State.initializeAsterage1State();
		asterage2State.initializeAsterage2State();
		
		//release the lock.
		stateLock.release();
	} 
	
}
