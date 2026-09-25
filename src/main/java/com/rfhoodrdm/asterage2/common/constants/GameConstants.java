package com.rfhoodrdm.asterage2.common.constants;

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
    public static final String GAME_VERSION = "v1.1.0";
	
    //how many frames per second are rendered to the screen.
    public static final int FRAMES_PER_SECOND = 32;
    
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
	
	
	public static final int GAME_PANEL_WIDTH = 1280;
	public static final int GAME_BOARD_HEIGHT = 960;
	public static final int GAME_HUD_HEIGHT = 100;

} 
