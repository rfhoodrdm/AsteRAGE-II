package com.rfhoodrdm.asterage2.controller;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.controller.asterage1.Asterage1Controller;
import com.rfhoodrdm.asterage2.controller.asterage2.Asterage2Controller;
import com.rfhoodrdm.asterage2.controller.title.TitleController;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.State;

import lombok.extern.slf4j.Slf4j;

/**
 * Class controller is the top level game logic implementation class.
 * It in turn invokes the game logic to update the game state for the splash screen, and either AsteRAGE 1 or AsteRAGE 2
 */


@Component
@Slf4j
public class Controller
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    private final State state;
    private final GUI gui;
	
    private final TitleController titleController;
    private final Asterage1Controller asterage1Controller;
    private final Asterage2Controller asterage2Controller;
	
    /*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Controller(State state, GUI gui, 
			TitleController titleController, Asterage1Controller asterage1Controller, Asterage2Controller asterage2Controller) {
		this.state = state;
		this.gui = gui;
		this.titleController = titleController;
		this.asterage1Controller = asterage1Controller;
		this.asterage2Controller = asterage2Controller;
	} 

	
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 * This method invokes the state update logic for the corresponding active game state.
	 */
	public void updateState () {
		CurrentState whatState = state.getCurrentState();
		
		switch ( whatState ) {
			case TITLE_SCREEN -> titleController.updateState();
			case ASTERAGE_1 ->	asterage1Controller.updateAsterage1State();
			case ASTERAGE_2 ->	asterage2Controller.updateState();
		} 
	} 
	
	/**
	 * Organizes the switch of the current, officially active state between Title screen, Asterage 1, and Asterage 2.
	 */
//	public void switchActiveState ( CurrentState newState ) {
//		//change and reinitialize the current active state.
//		state.changeCurrentActiveState( newState );
//		
//		//initialize the respective games.
//		asterage2Controller.beginNewGame();
//		
//		//show the correct gui components.
//		gui.changeCurrentGuiShown();
//	} 
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Receives key press and release events from the keyboard input module, and decides
	 * how to process them, according to which is the active state. Only the active controller should be
	 * receiving keyboard input.
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent ) {
		
		log.debug("Processing key event. Code: {}    Event Type: {}", keyCode, whichEvent);
		CurrentState currentActiveState = state.getCurrentState();
		switch (currentActiveState) {
			case TITLE_SCREEN 	-> titleController.processKeyEvent ( keyCode, whichEvent );
			case ASTERAGE_1 	-> asterage1Controller.processKeyEvent ( keyCode, whichEvent );
			case ASTERAGE_2 	-> asterage2Controller.processKeyEvent ( keyCode, whichEvent );
		}
	} 
} 
