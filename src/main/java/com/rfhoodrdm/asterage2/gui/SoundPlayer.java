package gui;

import javax.sound.sampled.*;

/**
 * SoundPlayer is responsible for playing sound and music on command from the SoundManager.
 * It is one piece of the overall sound system of the game.
 * 
 * Will probably want to static import the sound player, since we only had
 * to wrap it in a class so we could load the sounds from a non-static object.
 */
public class SoundPlayer
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
 
    
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
  
    public SoundPlayer( )
    {
		
    } //end constructor
    
    /*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
    
    
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
    
    
    /*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
    public static enum Sound 
    {
		//Data Members
		//List of sounds
		// FOR TITLE SCREEN.
		DEFAULT ( "DEFAULT.aif", SOUND_TYPE.SOUND_EFFECT ),
		GAME_SELECTED( "GAME_SELECTED.aif", SOUND_TYPE.SOUND_EFFECT),
		MENU_OPTION_CHANGED ( "MENU_OPTION_CHANGED.aif", SOUND_TYPE.SOUND_EFFECT),
		
		//FOR ASTERAGE 1 GAME SCREEN
		//Sound effects
		ENEMY_APPEARS("ENEMY_APPEARS.aif", SOUND_TYPE.SOUND_EFFECT),
		BIG_ENEMY_APPEARS("BIG_ENEMY_APPEARS.aif", SOUND_TYPE.SOUND_EFFECT),
		PLAYER_BULLET_FIRE("PLAYER_BULLET_FIRE.aif", SOUND_TYPE.SOUND_EFFECT),
		ENEMY_BULLET_FIRE("ENEMY_BULLET_FIRE.aif", SOUND_TYPE.SOUND_EFFECT),
		CREW_SCREAMS("CREW_SCREAMS.aif", SOUND_TYPE.SOUND_EFFECT),
		PLAYER_SHIELD_IMPACT("PLAYER_SHIELD_IMPACT.aif", SOUND_TYPE.SOUND_EFFECT),
		ENEMY_SHIELD_IMPACT("ENEMY_SHIELD_IMPACT.aif", SOUND_TYPE.SOUND_EFFECT),
		ASTEROID_IMPACT("ASTEROID_IMPACT.aif", SOUND_TYPE.SOUND_EFFECT),
		//ENGINE_THRUST("ENGINE_THRUST.aif", SOUND_TYPE.SOUND_EFFECT),
		ENEMY_SHIP_DESTROYED("ENEMY_SHIP_DESTROYED.aif", SOUND_TYPE.SOUND_EFFECT),
		PLAYER_SHIP_DESTROYED("PLAYER_SHIP_DESTROYED.aif", SOUND_TYPE.SOUND_EFFECT),
		GIANT_LASER_FIRE("GIANT_LASER_FIRE.aif", SOUND_TYPE.SOUND_EFFECT),
		PLAYER_SHIP_APPEARS("PLAYER_SHIP_APPEARS.aif", SOUND_TYPE.SOUND_EFFECT),
		ALIEN_EMERGES("ALIEN_EMERGES.aif", SOUND_TYPE.SOUND_EFFECT),
		REWARD_EARNED("REWARD_EARNED.aif", SOUND_TYPE.SOUND_EFFECT ),
		TRACTOR_BEAM_DEPLOYED("TRACTOR_BEAM_DEPLOYED.aif", SOUND_TYPE.SOUND_EFFECT ),
		
		
		//FOR ASTERAGE 2 GAME SCREEN   A2_Troll_Appears
		A2_Asteroid_Impact				("A2_Asteroid_Impact.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_ENEMY_PLASMA_BOLT_FIRE		("A2_ENEMY_PLASMA_BOLT_FIRE.aif",		SOUND_TYPE.SOUND_EFFECT ),
		A2_EXTRA_LIFE_AWARDED			("A2_EXTRA_LIFE_AWARDED.aif",			SOUND_TYPE.SOUND_EFFECT ),
		A2_Mythicite_Pickup				("A2_Mythicite_Pickup.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_Player_Destroyed				("A2_Player_Destroyed.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_PLAYER_HOMING_MISSILES_FIRED ("A2_PLAYER_HOMING_MISSILES_FIRED.aif",	SOUND_TYPE.SOUND_EFFECT ),
		A2_PLAYER_PLASMA_BOLT_FIRE		("A2_PLAYER_PLASMA_BOLT_FIRE.aif",		SOUND_TYPE.SOUND_EFFECT ),
		A2_Player_Shield_Impact			("A2_Player_Shield_Impact.aif",			SOUND_TYPE.SOUND_EFFECT ),
		A2_Player_Sonic_Disruptor_Active("A2_Player_Sonic_Disruptor_Active.aif",SOUND_TYPE.SOUND_EFFECT ),
		A2_PLAYER_SPAWNS				("A2_PLAYER_SPAWNS.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_Purchase_Denied				("A2_Purchase_Denied.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_Special_Power_Up_Pickup		("A2_Special_Power_Up_Pickup.aif",		SOUND_TYPE.SOUND_EFFECT ),
		A2_Troll_Appears					("A2_Troll_Appears.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_Troll_Destroyed				("A2_Troll_Destroyed.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_TROLL_LASER_FIRED			("A2_TROLL_LASER_FIRED.aif",			SOUND_TYPE.SOUND_EFFECT ),
		A2_Troll_Mothership_Appears		("A2_Troll_Mothership_Appears.aif",		SOUND_TYPE.SOUND_EFFECT ),
		A2_Troll_Shield_Impact			("A2_Troll_Shield_Impact.aif",			SOUND_TYPE.SOUND_EFFECT ),
		A2_Victory_Fanfare				("A2_Victory_Fanfare.aif",				SOUND_TYPE.SOUND_EFFECT ),
		A2_Warping_Out					("A2_Warping_Out.aif",					SOUND_TYPE.SOUND_EFFECT ),
		
		//music
		ASTERAGE_CLASSIC_MAIN_THEME			("ASTERAGE_CLASSIC_MAIN_THEME.aif",				SOUND_TYPE.MUSIC_TRACK ),
		ASTERAGE_CLASSIC_GAME_OVER_THEME	("ASTERAGE_CLASSIC_GAME_OVER_THEME.aif",		SOUND_TYPE.MUSIC_TRACK ),
		ASTERAGE_CLASSIC_CELEBRATION		("AsteRAGE_CLASSIC_CELEBRATION.aif",			SOUND_TYPE.MUSIC_TRACK ),
		
		
		
		A2_TRACK1_AsteRAGE_Begins		("A2_TRACK1_AsteRAGE_Begins.aif",			SOUND_TYPE.MUSIC_TRACK ),
		A2_TRACK2_ASTRAL_JOURNEY		("A2_TRACK2_ASTRAL_JOURNEY.aif",			SOUND_TYPE.MUSIC_TRACK ),
		A2_TRACK3_DESCENT				("A2_TRACK3_DESCENT.aif",					SOUND_TYPE.MUSIC_TRACK ),
		A2_TRACK4_NOSTALGIA				("A2_TRACK4_NOSTALGIA.aif",					SOUND_TYPE.MUSIC_TRACK ),
		A2_TRACK5_SKYS_THE_LIMIT		("A2_TRACK5_SKY'S_THE_LIMIT.aif",			SOUND_TYPE.MUSIC_TRACK ),
		A2_TRACK6_SPACE_MINER_DIRGE		("A2_TRACK6_SPACE_MINER_DIRGE.aif",			SOUND_TYPE.MUSIC_TRACK );
		
		

		//List of Music.

		final private String filename;
		private Clip clip;
		final private SOUND_TYPE soundType;

		//Constructor
		Sound ( String passedFilename, SOUND_TYPE passedType )
		{
			this.filename = passedFilename;
			this.soundType = passedType;
		} //end constructor
		
		public String getFilename ()
		{
			return this.filename;
		} //end function getKey


		public void setClip ( Clip passedClip )
		{
			this.clip = passedClip;
		} 

		//Function play plays the sound clip once, starting from the beginning. If the sound is already playing,
		//then it stops, and rewinds to the beginning of the clip before playing.
		public void play()
		{
			if (clip.isRunning())
			{
			clip.stop();
			}
			clip.setFramePosition(0);
			clip.start();

		} //end function play


		// Function playContinuously sets the sound clip to play in a never-ending loop. If the sound is already playing,
		//then it stops, rewinds to the beginning, and starts playing again.
		public void playContinuously()
		{
			if (clip.isRunning())
			{
			clip.stop();
			}
			clip.setFramePosition(0);
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		} //end function playContinuously

		/**
		 * Function playOnce checks to see if the sound is currently playing. If not, then it starts a new play of the sound.
		 */
		public void playOnce()
		{
			if ( false == clip.isRunning() )
			{
				clip.setFramePosition(0);
				clip.start();
			}
		} //end function playOnce
		
		//function haltPlaying stops a currently playing clip.
		public void haltPlaying()
		{
			if (clip.isRunning())
			{
			clip.stop();
			clip.setFramePosition(0);
			}
		} //end function haltPlaying


		/**
		 * function checkIfPlaying returns a true or false value depending on whether the selected clip is currently playing or not.
		 * @return True/false value of whether the sound is playing currently. True = yes, false = no.
		 */
		public boolean isPlaying()
		{
			return clip.isRunning();

		} //end function checkIfPlaying
		
		
		/**
		 * Is this sound designated a sound effect? (vs. a music track.)
		 * @return boolean representing if the sound is indeed a sound effect.
		 */
		public boolean isSoundEffect ()
		{
			if ( SOUND_TYPE.SOUND_EFFECT == this.soundType )
			{
				return true;
			} 
			//else
			return false;
		} //end function isSoundEffect
		
		/**
		 * Is this sound designated a music track? (vs. a sound effect.)
		 * @return boolean representing if the sound is indeed a music track.
		 */
		public boolean isMusicTrack ()
		{
			if ( SOUND_TYPE.MUSIC_TRACK == this.soundType )
			{
				return true;
			} 
			//else
			return false;
		} //end function isMusicTrack
		
    } //end enum Sound definition
	
	public enum SOUND_TYPE
	{
		SOUND_EFFECT,
		MUSIC_TRACK;
	} 
} //end class SoundPlayer definition
