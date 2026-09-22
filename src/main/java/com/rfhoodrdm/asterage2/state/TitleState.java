
package com.rfhoodrdm.asterage2.state;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StarPoint;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TitleState {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int NUMBER_STARS_IN_FIELD = 100;
	
	@Getter	private final ArrayList<StarPoint> starPointList = new ArrayList<>();
	@Getter	private int timeToGameStart;
	private int timeToNextActivity;
	private TITLE_SCREEN_ACTIVITY currentActivity;

	@Getter @Setter	private GAME_SELECTION currentGameSelected;
	@Getter @Setter	private boolean gameSelected;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TitleState () {
		initializeTitleState();
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public TITLE_SCREEN_ACTIVITY getCurrentActivity()
	{
		return this.currentActivity;
	} 
	
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
	} 
	
	/**
	 * Decrements the timer for the current activity, and chooses the next one, if applicable.
	 * Called every update tick.
	 * @return Boolean value that shows whether a new activity was started. True = yes, false = no.
	 */
	public boolean decrementTimeToNextActivity() {
		if (timeToNextActivity > 0) {
			timeToNextActivity -= 1;
			return false;
		} 
		else {
			setCurrentActivity( getCurrentActivity().getNextActivity() );
			return true;
		} 
	} 

	public void decrementTimeToGameStart ()	{
		this.timeToGameStart -= 1;
	} 
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	public void initializeTitleState()
	{
		setCurrentActivity(TITLE_SCREEN_ACTIVITY.DISPLAYING_TITLE);
		timeToGameStart = 2 * GameConstants.FRAMES_PER_SECOND;
		currentGameSelected = GAME_SELECTION.ASTERAGE2;
		gameSelected = false;
		
		createNewStarPointList();
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum TITLE_SCREEN_ACTIVITY {
		DISPLAYING_TITLE(10),
		DISPLAYING_STORY(10),
		DISPLAYING_HIGH_SCORE(10);
		
		int secondsToNextActivity;			
		
		//Constructor
		TITLE_SCREEN_ACTIVITY(int secondsToNext ) {
			this.secondsToNextActivity = secondsToNext;
		} 
		
		public int getTimeToNextActivity ()	{
			return this.secondsToNextActivity;
		} 
		
		/**
		 * gets the next activity for the title screen, in pre determined order.
		 */
		public TITLE_SCREEN_ACTIVITY getNextActivity () {
			switch ( this )
			{
				case DISPLAYING_TITLE:
					return DISPLAYING_HIGH_SCORE;
					
				case DISPLAYING_HIGH_SCORE:
					return DISPLAYING_STORY;
					
				case DISPLAYING_STORY:
				default:
					return DISPLAYING_TITLE;
			}
		} 
	} 
	
	public static enum GAME_SELECTION	{
		ASTERAGE1,
		ASTERAGE2;
		
		/**
		 * Gives the next game selection option when given the current one.
		 */
		public static GAME_SELECTION nextGameSelection ( GAME_SELECTION oldGameSelection )
		{
			switch ( oldGameSelection )
			{
				case ASTERAGE1: return ASTERAGE2;
				case ASTERAGE2: return ASTERAGE1;
				default: return ASTERAGE1;
			} 
		} 
	}
	
	private void createNewStarPointList() {
		starPointList.clear();
		for ( int count = 1;  count <= NUMBER_STARS_IN_FIELD;  ++count ) {
			starPointList.add(StarPoint.createNewRandomStarPoint());
		} 
	} 
} 
