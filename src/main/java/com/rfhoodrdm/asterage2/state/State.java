package com.rfhoodrdm.asterage2.state;

import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.state.asterage1.Asterage1State;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;
import com.rfhoodrdm.asterage2.state.title.TitleState;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * State is the top level class in the game's "model" which holds the game state.
 * Holds information pertaining to the splash screen, as well as AsteRAGE 1 and 2.
 */
@Slf4j
@Component
public class State
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */	
	private final TitleState titleState;
	private final Asterage1State asterage1State;
	private final Asterage2State asterage2State;
	
	//state semaphore lock, protecting against concurrent access.
	private final Semaphore stateLock;
	
	@Getter
	private CurrentState currentState;		//Which game state is the active one? Default is title screen.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public State(TitleState titleState, Asterage1State asterage1State, Asterage2State asterage2State) {
		this.titleState = titleState;
		this.asterage1State = asterage1State;
		this.asterage2State = asterage2State;
		
		//initialize the state semaphore lock to 1 permit, fairness enforced.
		stateLock = new Semaphore ( 1, true );
		
		//the initial currently active state is the title screen.
		 this.currentState = CurrentState.TITLE_SCREEN; 
	} 
	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	/**
	 * Changes the currently active state to a new one.
	 */
	public void changeCurrentActiveState( CurrentState newCurrentState ) {
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
