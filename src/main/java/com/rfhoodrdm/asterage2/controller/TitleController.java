/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.controller;

import com.rfhoodrdm.asterage2.state.TitleState;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.sounds.SoundManager;

import java.awt.event.KeyEvent;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 *
 * @author roberthood
 */
public class TitleController
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	TitleState titleState;
	GUI gui;						//master gui reference
	Controller controller;			//master controller reference
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void setTitleState ( TitleState passedTitleState )
	{
		this.titleState = passedTitleState;
	} //end function setTitleState
	public void updateState()
	{
		updateTitleState(); 
	} //end function updateState
	public void setGUI ( GUI passedGUI )
	{
		this.gui = passedGUI;
	} //end function setGUI
	public void setController ( Controller passedController ) 
	{
		this.controller = passedController;
	} //end function setController
	
	/**
	 * Entry point of the logic to determine how to react to key presses.
	 * @param key
	 * @param whichEvent 
	 */
	public void processKeyEvent ( int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent )
	{
		//First, filter out keystrokes that are not used, so we don't try to process them.
		if ( false == checkIfKeyUsed( keyCode ) )
		{
			return;	//don't try to process it.
		}
		
		//Key up, or key down?
		switch ( whichEvent )
		{
			case UP:
				processKeyUpEvent( keyCode );
				break;
				
			case DOWN:
				processKeyDownEvent( keyCode );
				break;
				
			default:
				//do nothing
				return;
		} //end switch based on key up or key down.
	} //end function processKeyEvent
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	private void processKeyUpEvent ( int keyCode )
	{
		//For State: SELECTING_GAME
		//For state: GAME_SELECTED_COUNTDOWN
		//For state: SLIDING_PANEL
		//For state: DISPLAYING_NEXT_PANEL
		
	} //end function processKeyEvent
	
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

			}//end switch 
		} //end if check to see if input is still being accepted.
	} //end function processKeyDownEvent
	
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
		} //end switch
	} //end function checkIfKeyUsed
	
	
	private void updateTitleState ()
	{
		//First find out what sactivity the title screen is performing. 
		TitleState.TITLE_SCREEN_ACTIVITY currentActivity = titleState.getCurrentActivity();
		
		//always-do tasks:
		gui.refreshTitleGameMenu();			//refresh the appearance of the game selection menu.
		gui.assertFocusOnFrame();			//keep the focus on the frame so we keep getting user input.
		
		
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
		gui.refreshSplashScreen();

		
	} //end function updateTitleState
	
	private void update_GameSelectedCountdown()
	{
		//if the countdown is still above 0, then deduct 1 from it.
		if ( titleState.getTimeToGameStart() > 0 )
		{
			titleState.decrementTimeToGameStart();
		} //end if block for time left.
		
		//if the countdown is 0 exactly, switch to the next screen.
		if ( 0 >= titleState.getTimeToGameStart () )
		{
			//switch to the game corresponding to the selection made
			TitleState.GAME_SELECTION gameSelection = titleState.getCurrentGameSelected();
			DebugManager.logMessage(5, "Countdown over. Game switching to : " + gameSelection);
			if ( gameSelection == TitleState.GAME_SELECTION.ASTERAGE1 )
			{
				controller.switchActiveState( GameConstants.CURRENT_STATE.ASTERAGE_1 );
			} //end if clause dealing with asterage 1 being the selected game.
			else if ( gameSelection == TitleState.GAME_SELECTION.ASTERAGE2 )
			{
				controller.switchActiveState( GameConstants.CURRENT_STATE.ASTERAGE_2 );
			} //end else-if to go to AsteRAGE 2
			else
			{
				//shouldn't reach here, because we've selected neither AsteRAGE 1 nor AsteRAGE 2. Make a note in the error log
				DebugManager.logMessage(2, "Cannot switch to game: Game selection is unknown.");
			} //end else clause for 
			
		} //end if block for no time left.
	} //end function
	
	/**
	 * Change the currently active menu selection.
	 */
	private void menuSelectionNavigateDown()
	{
		//Change the menu selection.
		TitleState.GAME_SELECTION currentGameSelection = titleState.getCurrentGameSelected();
		TitleState.GAME_SELECTION nextGameSelection = TitleState.GAME_SELECTION.nextGameSelection(currentGameSelection);
		
		DebugManager.logMessage(5, "Changing game selected: " + nextGameSelection);
		titleState.setCurrentGameSelected(nextGameSelection);
		
		//Play the menu selection changed sound.
		gui.playSoundForEvent( SoundManager.SOUND_EVENT.TITLE_SCREEN_MENU_OPTION_CHANGED );
	} //end function changeMenuSelection
	
	/**
	 * Finalize the selection when the selecting key is pressed.
	 */
	private void menuSelectGame()
	{
		//change the title screen state to game selected.
		titleState.setGameSelected(true);
		
		//play the game selection sound.
		gui.playSoundForEvent ( SoundManager.SOUND_EVENT.TITLE_SCREEN_MENU_GAME_SELECTED );
	} //end function menuSelectGame
} //end class TitleController definition.
