package com.rfhoodrdm.asterage2.state;

/**
 * Contains information about one high score entry. Also has the getter and setter methods for those stats.
 * @author roberthood
 */
public class HighScoreEntry
implements Comparable<HighScoreEntry>
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private String HighScoreName;
	private int HighScoreLevel;
	private long HighScorePoints;
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	//Cosntructor with three arguments.
	public HighScoreEntry( String passedName, int passedLevel, long passedPoints )
	{
		this.HighScoreName = passedName;
		this.HighScoreLevel= passedLevel;
		this.HighScorePoints = passedPoints;
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	@Override
	public int compareTo( HighScoreEntry anotherInstance )
	{
		int response;
		
		if ( getHighScorePoints() > anotherInstance.getHighScorePoints() )
		{
			response = -1;
		}
		else
		{
			response = 1;
		}
		return response;
	} 
	
	/**
	 * @return the HighScoreName
	 */
	public String getHighScoreName()
	{
		return HighScoreName;
	}

	/**
	 * @param HighScoreName the HighScoreName to set
	 */
	public void setHighScoreName(String HighScoreName)
	{
		this.HighScoreName = HighScoreName;
	}

	/**
	 * @return the HighScoreLevel
	 */
	public int getHighScoreLevel()
	{
		return HighScoreLevel;
	}

	/**
	 * @param HighScoreLevel the HighScoreLevel to set
	 */
	public void setHighScoreLevel(int HighScoreLevel)
	{
		this.HighScoreLevel = HighScoreLevel;
	}

	/**
	 * @return the HighScorePoints
	 */
	public long getHighScorePoints()
	{
		return HighScorePoints;
	}

	/**
	 * @param HighScorePoints the HighScorePoints to set
	 */
	public void setHighScorePoints(long HighScorePoints)
	{
		this.HighScorePoints = HighScorePoints;
	}
} //end class HighScoreEntry definition
