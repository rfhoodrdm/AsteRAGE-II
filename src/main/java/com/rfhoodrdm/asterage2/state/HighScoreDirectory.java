package com.rfhoodrdm.asterage2.state;

import java.util.ArrayList;
import java.util.Collections;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.utility.DebugManager;



/**
 *
 * @author roberthood
 */
public class HighScoreDirectory
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private ArrayList<HighScoreEntry> highScoreList;
	GAME_IDENTIFIER gameIdentifier;
	DataLoader dataLoader;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/**
	 * Get the loaded high score listing for this directory.
	 * @param whichGame Identifier as to whether this game is for AsteRAGE 1 or 2.
	 * @param dataLoader Dataloader reference, so we can load the high scores.
	 */
	public HighScoreDirectory( HighScoreDirectory.GAME_IDENTIFIER whichGame, DataLoader passedLoader )
	{
		gameIdentifier = whichGame;			//which game is this listing for? Asterage 1 or 2?
		dataLoader = passedLoader;
		
		ArrayList<HighScoreEntry> loadedList = dataLoader.getAsterage1HighScoreList( whichGame );
		
		if ( (null == loadedList) || (loadedList.size() < 10) )
		{
			DebugManager.logMessage(5, "No list. Making a default one.");
			this.highScoreList = createDefaultList();
		} 
		else 
		{
			this.highScoreList = loadedList;
		}
		
		//Sort the list in any case.
		Collections.sort (highScoreList);
		
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Insert a new record into the AsteRAGE high scores! Create, insert, sort, save.
	 * @param name
	 * @param level
	 * @param points 
	 */
	public void insertNewRecord ( String name, int level, long points )
	{
		HighScoreEntry newEntry = new HighScoreEntry ( name, level, points );
		highScoreList.add(newEntry);
		Collections.sort (highScoreList);
		this.saveList();
	} 
	/**
	 * Search the list and see if the score the player has earned ranks in the top ten. If so, return true.
	 * Else return false.
	 * @param points
	 * @return Boolean value representing if the score places in the top ten. True = yes, false = no.
	 */
	public boolean doesScoreRankTopTen ( long points )
	{
		Collections.sort (highScoreList);
		long lowestScore = Long.parseLong( getPointsAtPlace (9) );	//place 9 holds the 10th entry -- starts at 0
		if ( points > lowestScore )
		{
			return true;	//just have to beat the lowest score on the top ten list.
		} 
		
		//else
		return false;
	} //end function doesScoreRankTopTen
	
	/**
	 * Gets the name for a high score entry.
	 * @param place
	 * @return 
	 */
	public String getNameAtPlace( int place )
	{
		//check if there is such an entry. If so, return it. If not, then return a default value.
		if ( place < this.highScoreList.size() )
		{
			return highScoreList.get(place).getHighScoreName();
		}
		
		//else
		DebugManager.logMessage(5, "Tried to get a high score entry that didn't exist.");
		return "AsteRAGE";
	}
	/**
	 * Gets the level for a high score entry.
	 * @param place
	 * @return 
	 */
	public String getLevelAtPlace ( int place )
	{
		//check if there is such an entry. If so, return it. If not, then return a default value.
		if ( place < this.highScoreList.size() )
		{
			return "" + highScoreList.get(place).getHighScoreLevel();
		}
		
		//else
		DebugManager.logMessage(5, "Tried to get a high score entry that didn't exist.");
		return "10";
	} 
	/**
	 * Gets the points for a high score entry.
	 * @param place
	 * @return 
	 */
	public String getPointsAtPlace ( int place )
	{
		//check if there is such an entry. If so, return it. If not, then return a default value.
		if ( place < this.highScoreList.size() )
		{
			return "" + highScoreList.get(place).getHighScorePoints();
		}
		
		//else
		DebugManager.logMessage(5, "Tried to get a high score entry that didn't exist.");
		return "25000";
	}
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Invoke the data loader save feature.
	 */
	public void saveList()
	{
		dataLoader.saveHighScoreList(highScoreList, this.gameIdentifier.toFileNameString() );
	} //end function saveList
	
	public ArrayList<HighScoreEntry> createDefaultList()
	{
		ArrayList<HighScoreEntry> defaultList = new ArrayList<>();
		
		String[] nameArray1 = {	"AsteRAGE", 
								"AsteRAGE", "AsteRAGE",
								"AsteRAGE", "ANTHONY L",
								"DAVID W", "SAMUEL B", 
								"KENNY M", "PENNY P", 
								"JAMES B", "ROBERT H", 
								};
		String[] nameArray2 = {	"AsteRAGE",
								"AsteRAGE", "AsteRAGE",
								"CASSANDRA N", "JAY L", 
								"DONALD R", "RAYNOR M", 
								"RAYDEL M", "JASON DB", 
								"JUSTIN F", "NATHAN T", 
								};
		
		String[] nameArrayToUse;
		
		//pick which name array to use to create the default high score slots.
		if ( GAME_IDENTIFIER.ASTERAGE1 == this.gameIdentifier )
		{
			nameArrayToUse = nameArray1;
		} 
		else
		{
			nameArrayToUse = nameArray2;
		}
			
		for ( int count = 1; count <= 10; ++count)
		{
			String name = nameArrayToUse[count];
			int level = count;
			long points = 2500 * count;
			HighScoreEntry newEntry = new HighScoreEntry ( name, level, points );
			
			defaultList.add( newEntry );
		} //end for loop to create 10 fake entries.
		
		return defaultList;
	}//end function createDefaultList
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum GAME_IDENTIFIER
	{
		ASTERAGE1,
		ASTERAGE2;
		
		/**
		 * Get the file name that the file should be saved under.
		 * @return 
		 */
		public String toFileNameString()
		{
			switch (this )
			{
				case ASTERAGE1:
					return GameConstants.ASTERAGE_1_HIGH_SCORE_FILE_NAME;
				case ASTERAGE2:
					return GameConstants.ASTERAGE_2_HIGH_SCORE_FILE_NAME;
				default:
					return "default";
			} 
		}
	} //end enumerated type GAME_IDENTIFIER definition
	
} //end class HighScoreDirectory definition.
