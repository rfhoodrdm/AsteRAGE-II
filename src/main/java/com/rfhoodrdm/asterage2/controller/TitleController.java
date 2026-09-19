package com.rfhoodrdm.asterage2.controller;

import java.awt.event.KeyEvent;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.sounds.SoundEvent;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.TitleState;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TitleController
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	@Getter 
	@Setter
	private TitleState titleState;
	
	@Getter 
	@Setter
	private GUI gui;						//master gui reference
	
	@Getter 
	@Setter
	private Controller controller;			//master controller reference
	
	private final SoundManager soundManager;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TitleController(SoundManager soundManager) {
		this.soundManager = soundManager;
	}
	
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	public void updateState()	{
		updateTitleState(); 
	}
	
	/**
	 * Entry point of the logic to determine how to react to key presses.
	 * @param key
	 * @param whichEvent 
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent )
	{
		//First, filter out keystrokes that are not used, so we don't try to process them.
		if ( false == checkIfKeyUsed( keyCode ) ) {
			return;	
		}
		
		//Key up, or key down?
		switch ( whichEvent ) {
			case UP:
				processKeyUpEvent( keyCode );
				break;
				
			case DOWN:
				processKeyDownEvent( keyCode );
				break;
				
			default:
				return;
		} 
	}
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	private void processKeyUpEvent ( int keyCode )
	{
		//For State: SELECTING_GAME
		//For state: GAME_SELECTED_COUNTDOWN
		//For state: SLIDING_PANEL
		//For state: DISPLAYING_NEXT_PANEL
		
	} 
	
	private void processKeyDownEvent ( int keyCode )
	{
		//Is a game currently selected, and the game is counting down to start? If so, we don't accept
		//any more input. But if not, then we toggle the selected game.
		if ( false == titleState.isGameSelected() )
		{
			switch ( keyCode )
			{ 
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
	 * @param key
	 * @return Boolean value representing the decision of if the key is a used key. True = yes, false = no.
	 */
	private boolean checkIfKeyUsed ( int keyCode )
	{
		switch ( keyCode )
		{
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
	
	private void updateTitleState () {
		//First find out what sactivity the title screen is performing. 
		TitleState.TITLE_SCREEN_ACTIVITY currentActivity = titleState.getCurrentActivity();
		
		//always-do tasks:
		gui.repaint();			//refresh the appearance of the game selection menu.
		
		//do different things based on what the current activity is.
		// If the game has been selected, then update the countdown. 
		if ( titleState.isGameSelected() )
		{
			update_GameSelectedCountdown();
			return;
		}
		
		//else decrement the activity counter.
		//redraw the screen. --Changed to redraw with every update to accomodate new star field pattern.
		titleState.decrementTimeToNextActivity();
		gui.repaint();
	} 
	
	private void update_GameSelectedCountdown()
	{
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
				controller.switchActiveState( CurrentState.ASTERAGE_1 );
			} else if ( gameSelection == TitleState.GAME_SELECTION.ASTERAGE2 ) {
				controller.switchActiveState( CurrentState.ASTERAGE_2 );
			} else {
				//shouldn't reach here, because we've selected neither AsteRAGE 1 nor AsteRAGE 2. Make a note in the error log
				log.error("Cannot switch to game: Game selection is unknown.");
			} 
		} 
	} 
	
	/**
	 * Change the currently active menu selection.
	 */
	private void menuSelectionNavigateDown()
	{
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
	private void menuSelectGame()
	{
		//change the title screen state to game selected.
		titleState.setGameSelected(true);
		
		//play the game selection sound.
		soundManager.playSoundEvent ( SoundEvent.TITLE_SCREEN_MENU_GAME_SELECTED );
	} 
} 