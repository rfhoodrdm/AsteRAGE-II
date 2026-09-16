package com.rfhoodrdm.asterage2.utility;

/**
 * GameConstants provides enumerated and hard-coded constants that apply to the entire game,
 * but don't have a relevant package elsewhere.
*/
public class GameConstants
{
    
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
    
    //Working title of the game.
    public static final String GAME_NAME = "AsteRAGE 2";
    public static final String GAME_VERSION = "v1.0.0";
	
    //how many frames per second are rendered to the screen.
    public static final int FRAMES_PER_SECOND = 32;
    
	//what level of debugging message should be used.
    public static final int DEBUGGING_LEVEL = 5;
	
	//length of misc thread sleeping time between activity. Used for performance tuning.
	public static final int THREAD_SLEEP_TIME = 50;
	
	//official name of the font used in the game
	public static final String gameFont = "Skia";
	public static final String monospaceGameFont = "Courier New";
	public static final int Asterage2HUDFontSize = 20;
	
	//how many points are needed before an extra life is granted?
	public static final int pointsForExtraLifeAward = 10000;
	
	//what is the lowest level that a troll mothership may appear?  (This level + 1) 
	public static final int TrollMotherShipLevelDecrement = 6;	
	
	//High Score constants
	//Name of the AsteRAGE high score files
	public static final String ASTERAGE_1_HIGH_SCORE_FILE_NAME = "AsteRAGE1_HighScores.xml";
	public static final String ASTERAGE_2_HIGH_SCORE_FILE_NAME = "AsteRAGE2_HighScores.xml";
	public static final String HIGH_SCORE_ROOT_NODE_NAME = "HighScores";
	public static final String HIGH_SCORE_ENTRY_NODE = "HighScoreEntryNode";
	public static final String HIGH_SCORE_NAME_ATTRIBUTE = "Name";
	public static final String HIGH_SCORE_LEVEL_ATTRIBUTE = "Level";
	public static final String HIGH_SCORE_POINTS_ATTRIBUTE = "Points";
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	//what different states are possible?
	public enum CURRENT_STATE
	{
		TITLE_SCREEN,
		ASTERAGE_1,
		ASTERAGE_2;
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case TITLE_SCREEN: return "Title Screen.";
				case ASTERAGE_1: return "AsteRAGE 1";
				case ASTERAGE_2: return "AsteRAGE 2";
				default: return "";
			} //end switch based on type
		} //end function toString
	} //end definition of enumerated value for current state.
	
	
	
	
} //end class GameConstants definition
