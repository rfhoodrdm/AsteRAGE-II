package com.rfhoodrdm.asterage2.controller.title;

import java.awt.event.KeyEvent;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StarPoint;
import com.rfhoodrdm.asterage2.sounds.SoundEvent;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.State;
import com.rfhoodrdm.asterage2.state.title.TitleState;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TitleController
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private final GUI gui;
	private final State state;
	private final TitleState titleState;
	private final SoundManager soundManager;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TitleController(GUI gui, State state, TitleState titleState, SoundManager soundManager) {
		this.gui = gui;
		this.state = state;
		this.titleState = titleState;
		this.soundManager = soundManager;
	}
	
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	public void updateState()	{
		updateTitleState(); 
	}
	
	/**
	 * Entry point to determine reaction to a key event.
	 * First, decide if we care about that particular key, then determine if it was a key-up or key-down event.
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent ) {
		//First, filter out keystrokes that are not used, so we don't try to process them.
		if ( false == checkIfKeyUsed( keyCode ) ) {
			return;	
		}
		
		switch ( whichEvent ) {
			case UP -> 		processKeyUpEvent( keyCode );
			case DOWN ->	processKeyDownEvent( keyCode );
		} 
	}
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Placeholder for key up event processing.
	 */
	private void processKeyUpEvent ( int keyCode ) {
		//For State: SELECTING_GAME
		//For state: GAME_SELECTED_COUNTDOWN
		//For state: SLIDING_PANEL
		//For state: DISPLAYING_NEXT_PANEL
	} 
	
	private void processKeyDownEvent ( int keyCode ) {
		//Is a game currently selected, and the game is counting down to start? If so, we don't accept
		//any more input. But if not, then we toggle the selected game.
		if ( false == titleState.isGameSelected() )	{
			switch ( keyCode ) { 
				case KeyEvent.VK_W:
				case KeyEvent.VK_S:
				case KeyEvent.VK_D:
				case KeyEvent.VK_A:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
				case KeyEvent.VK_KP_UP:
				case KeyEvent.VK_KP_LEFT:
				case KeyEvent.VK_KP_RIGHT:
					menuSelectionNavigateDown();
					break;

				case KeyEvent.VK_K:
				case KeyEvent.VK_SPACE:
					menuSelectGame();
					break;
			}
		} 
	} 
	
	/**
	 * Check the keystroke against our list of used keys. 
	 * If the key is used, return true. Else return false.
	 */
	private boolean checkIfKeyUsed ( int keyCode )
	{
		switch ( keyCode ) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_S:
			case KeyEvent.VK_A:
			case KeyEvent.VK_D:
			case KeyEvent.VK_K:
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_KP_LEFT:
			case KeyEvent.VK_KP_RIGHT:
				return true;
			
			default:
				return false;
		} 
	} 
	
	/**
	 * Always animate the star background.
	 * Then, depending on whether a game has been selected or not, 
	 * either update the countdown or switch to the game screen in question.
	 */
	private void updateTitleState () {

		moveStars();

		if ( titleState.isGameSelected() ) {
			update_GameSelectedCountdown();
			return;
		}
		titleState.decrementTimeToNextActivity();
	} 
	
	private void update_GameSelectedCountdown()	{
		//if the countdown is still above 0, then deduct 1 from it.
		if ( titleState.getTimeToGameStart() > 0 )	{
			titleState.decrementTimeToGameStart();
		}
		
		//if the countdown is 0 exactly, switch to the next screen.
		if ( 0 >= titleState.getTimeToGameStart() ) {
			//switch to the game corresponding to the selection made
			TitleState.GAME_SELECTION gameSelection = titleState.getCurrentGameSelected();
			log.debug("Countdown over. Game switching to : {}", gameSelection);
			if ( gameSelection == TitleState.GAME_SELECTION.ASTERAGE1 ) {
				switchActiveState( CurrentState.ASTERAGE_1 );
			} else if ( gameSelection == TitleState.GAME_SELECTION.ASTERAGE2 ) {
				switchActiveState( CurrentState.ASTERAGE_2 );
				soundManager.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_2_TRACK1);	//TODO: possibly refactor this to somewhere more sensible?
			} else {
				//shouldn't reach here, because we've selected neither AsteRAGE 1 nor AsteRAGE 2. Make a note in the error log
				log.error("Cannot switch to game: Game selection is unknown.");
			} 
		} 
	} 
	
	private void switchActiveState(CurrentState newState) {
		state.changeCurrentActiveState( newState );
		gui.changeCurrentGuiShown();					
	}

	/**
	 * Change the currently active menu selection.
	 */
	private void menuSelectionNavigateDown() {
		//Change the menu selection.
		TitleState.GAME_SELECTION currentGameSelection = titleState.getCurrentGameSelected();
		TitleState.GAME_SELECTION nextGameSelection = TitleState.GAME_SELECTION.nextGameSelection(currentGameSelection);
		
		log.debug("Changing game selected: {}", nextGameSelection);
		titleState.setCurrentGameSelected(nextGameSelection);
		
		//Play the menu selection changed sound.
		soundManager.playSoundEvent( SoundEvent.TITLE_SCREEN_MENU_OPTION_CHANGED );
	} 
	
	/**
	 * Finalize the selection when the selecting key is pressed.
	 */
	private void menuSelectGame() {
		//change the title screen state to game selected.
		titleState.setGameSelected(true);
		
		//play the game selection sound.
		soundManager.playSoundEvent ( SoundEvent.TITLE_SCREEN_MENU_GAME_SELECTED );
	} 
	
	private void moveStars()	{
		List<StarPoint> starPointList =  titleState.getStarPointList();
		for ( StarPoint currentStar: starPointList )		{
			currentStar.moveStar();
			if ( currentStar.checkExpired() ) {
				currentStar.reset();
			} 
		} 
	} 
} 