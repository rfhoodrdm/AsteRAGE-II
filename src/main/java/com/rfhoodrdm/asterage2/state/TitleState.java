/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.state;

import lombok.extern.slf4j.Slf4j;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;

/**
 *
 * @author roberthood
 */



@Slf4j
public class TitleState
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	TITLE_SCREEN_ACTIVITY currentActivity;
	int timeToGameStart;
	int timeToNextActivity;
	GAME_SELECTION currentGameSelected;
	boolean gameSelected;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TitleState ()
	{
		initializeTitleState();
	} //end TitleState constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public TITLE_SCREEN_ACTIVITY getCurrentActivity()
	{
		return this.currentActivity;
	} //end function get current activity
	
	/**
	 * Set the current activity to the one passed, and set the countdown timer to the next one.
	 * @param passedActivity 
	 */
	public void setCurrentActivity ( TITLE_SCREEN_ACTIVITY passedActivity)
	{
		this.currentActivity = passedActivity;
		//one set of game ticks per second for this activity.
		this.timeToNextActivity = passedActivity.getTimeToNextActivity() * GameConstants.FRAMES_PER_SECOND;	
		log.debug("Setting title screen activity to {}", passedActivity);
	} //end function setCurrentActivity
	
	/**
	 * Decrements the timer for the current activity, and chooses the next one, if applicable.
	 * Called every update tick.
	 * @return Boolean value that shows whether a new activity was started. True = yes, false = no.
	 */
	public boolean decrementTimeToNextActivity()
	{
		//decrement if there is time left.
		if ( this.timeToNextActivity > 0 )
		{
			this.timeToNextActivity -= 1;
			return false;
		} 
		else //pick the next activity
		{
			setCurrentActivity ( getCurrentActivity().getNextActivity() );
			return true;
		} 
	} //end function dcrementTimeToNextActivity
	
	
	public GAME_SELECTION getCurrentGameSelected ()
	{
		return this.currentGameSelected;
	} //end getter for current game selected
	public void setCurrentGameSelected ( GAME_SELECTION passedGameSelection)
	{
		this.currentGameSelected = passedGameSelection;
	} //end function setCurrentGameSelected
	
	public int getTimeToGameStart ()
	{
		return this.timeToGameStart;
	} //end function getTimeToNextSlide
	public void decrementTimeToGameStart ()
	{
		this.timeToGameStart -= 1;
	} //end function decrementTimeToNextSlide
	public boolean isGameSelected()
	{
		return this.gameSelected;
	} 
	public void setGameSelected( boolean passedGameSelectedFlag)
	{
		this.gameSelected = passedGameSelectedFlag;
	} //end function setGameSelected
	
	
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	public void initializeTitleState()
	{
		setCurrentActivity(TITLE_SCREEN_ACTIVITY.DISPLAYING_TITLE);
		timeToGameStart = 2 * GameConstants.FRAMES_PER_SECOND;
		currentGameSelected = GAME_SELECTION.ASTERAGE2;
		gameSelected = false;
	} //end function initializeState
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum TITLE_SCREEN_ACTIVITY
	{
		DISPLAYING_TITLE(10),
		DISPLAYING_STORY(10),
		DISPLAYING_HIGH_SCORE(10);
		
		int timeToNextActivity;				//given in seconds.
		
		//Constructor
		TITLE_SCREEN_ACTIVITY(int passedTimeToNext )
		{
			this.timeToNextActivity = passedTimeToNext;
		} 
		
		public int getTimeToNextActivity ()
		{
			return this.timeToNextActivity;
		} //end function getTimeToNextActivity
		
		/**
		 * gets the next activity for the title screen, in pre determined order.
		 * @return 
		 */
		public TITLE_SCREEN_ACTIVITY getNextActivity ()
		{
			switch ( this )
			{
				case DISPLAYING_TITLE:
					return DISPLAYING_HIGH_SCORE;
					
				case DISPLAYING_HIGH_SCORE:
					return DISPLAYING_STORY;
					
				case DISPLAYING_STORY:
				default:
					return DISPLAYING_TITLE;
			} //end switch based on type.
		} //end function getNextActivity
		
	} //end enum TITLE_GENERAL_STATE
	
	public static enum GAME_SELECTION
	{
		ASTERAGE1,
		ASTERAGE2;
		
		/**
		 * Gives the next game selection option when given the current one.
		 * @param oldGameSelection
		 * @return 
		 */
		public static GAME_SELECTION nextGameSelection ( GAME_SELECTION oldGameSelection )
		{
			switch ( oldGameSelection )
			{
				case ASTERAGE1: return ASTERAGE2;
				case ASTERAGE2: return ASTERAGE1;
				default: return ASTERAGE1;
			} //end switch
		} //end function nextGameSelection
	}// end enum GAME_SELECTION
	
	
} //end class TitleState definition.
