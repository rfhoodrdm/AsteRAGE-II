package state;

/**
 * This package encapsulates the player's score and modifier functions. This is so a reference to it 
 * may be passed forward as needed to award points.
 */
public class Asterage1ScoreState
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	int playerScore;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Asterage1ScoreState()
	{
		resetPlayerScore();
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void resetPlayerScore ()
	{
		this.playerScore = 0;
	} //end function resetPlayerScore
	
	public void awardPoints (SCORE_SYSTEM pointsEvent )
	{
		this.playerScore += pointsEvent.getPointValue();
	} //end function awardPoints.
	
	public int getScore ()
	{
		return this.playerScore;
	} //end function getScore
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Define how many points are awarded for completing various tasks in the game.
	 */
	public static enum SCORE_SYSTEM
	{
		DESTROY_LARGE_ASTEROID(25),
		DESTROY_MEDIUM_ASTEROID(25),
		DESTROY_SMALL_ASTEROID(25),
		HIT_TROLL_SHIP(5),
		DESTROY_TROLL_SHIP(50),
		HIT_TROLL_MOTHERSHIP(5),
		PICK_UP_MYTHICITE(250),
		DESTROY_TROLL_MOTHERSHIP(1000);
		
		int pointValue;
		
		SCORE_SYSTEM ( int passedPointValue )
		{
			this.pointValue = passedPointValue;
		} 
		public int getPointValue()
		{
			return this.pointValue;
		} //end function getPoints
		
	} //end enum SCORE_SYSTEM definition.
} //end Asterage1ScoreState definition
