package com.rfhoodrdm.asterage2.sounds;

import java.util.concurrent.Semaphore;

import com.rfhoodrdm.asterage2.constants.GameConstants;


/**
 * This class determines when and for how long sounds and sound effects should be played for the client.
 * It is its own thread because it needs to be able to determine on its own when to cycle back to the start
 * of a song track.
 * 
 */
public class SoundManager
	extends Thread
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	SoundPlayer soundPlayer;				//the reference to the actual sound player object.
	Semaphore soundLock;					//semaphore protecting against concurrent access.
	int trackNumber;						//current track number in the music sequence currently being played.
	SOUNDTRACK_SEQUENCE currentSoundTrack;	//which set of songs is currently playing?
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SoundManager ( )
	{
		//create the SoundPlayer object.
		soundPlayer = new SoundPlayer ( );
		
		soundLock = new Semaphore ( 1, true );			//concurrency lock set to 1 permit, fairness enforced.
		
		currentSoundTrack = SOUNDTRACK_SEQUENCE.NONE;	//no song playing initially.
		trackNumber = 0;								//explicitly initialize to 0. We start counting from there.
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
    
	/**
	 * function playSoundEvent takes an enumerated value of an event as an argument
	 * and directs the SoundPlayer to play the associated sound. If no sound is associated, then none is played.
	 * @param theEvent Enumerated sound event for which we are playing a sound.
	 */
	public void playSoundEvent ( SoundManager.SOUND_EVENT theEvent )
	{
		//switch statement selects the sound based on what event has occurred.
		switch ( theEvent )
		{
			//Title screen events.
			case TITLE_SCREEN_MENU_OPTION_CHANGED:
				Sound.MENU_OPTION_CHANGED.play();
				break;
				
			case TITLE_SCREEN_MENU_GAME_SELECTED:
				Sound.GAME_SELECTED.play();
				break;
			
				
			//Asterage 1 game events
			case PLAYER_FIRES_BULLET:
				Sound.PLAYER_BULLET_FIRE.play();
				break;
			case ENEMY_FIRES_BULLET:
				Sound.ENEMY_BULLET_FIRE.play();
				break;
			case ASTEROID_IMPACT:
				Sound.ASTEROID_IMPACT.play();
				break;
			case PLAYER_SHIELD_IMPACT:
				Sound.PLAYER_SHIELD_IMPACT.playOnce();
				break;
			case ENEMY_SHIELD_IMPACT:
				Sound.ENEMY_SHIELD_IMPACT.playOnce();
				break;
			case ENEMY_APPEARS:
				Sound.ENEMY_APPEARS.play();
				break;
			case REWARD_EARNED:
				Sound.REWARD_EARNED.play();
				break;
			case PLAYER_SHIP_APPEARS:
				Sound.PLAYER_SHIP_APPEARS.play();
				break;
			case PLAYER_SHIP_DESTROYED:
				stopSoundEvent(SoundManager.SOUND_EVENT.PLAYER_SHIELD_IMPACT );		//stop playing shield impact noise if we're dead.
				Sound.PLAYER_SHIP_DESTROYED.play();
				break;
			case ENEMY_SHIP_DESTROYED:	
			case WEAPON_POD_DESTROYED:		
				Sound.ENEMY_SHIP_DESTROYED.play();
				break;
			case GIANT_LASER_FIRE:
				Sound.GIANT_LASER_FIRE.play();
				break;
			case ALIEN_EMERGES:
				Sound.ALIEN_EMERGES.play();
				break;
			case BIG_ENEMY_APPEARS:
				Sound.BIG_ENEMY_APPEARS.play();
				break;
			case TRACTOR_BEAM_DEPLOYED:
				Sound.TRACTOR_BEAM_DEPLOYED.playOnce();
				break;
			
				
			// ASTERAGE 2 GAME EVENTS:
			case A2_ASTEROID_IMPACT:
				Sound.A2_Asteroid_Impact.play();
				break;
			case A2_EXTRA_LIFE_AWARDED:
				Sound.A2_EXTRA_LIFE_AWARDED.play();
				break;
			case A2_ENEMY_PLASMA_BOLT_FIRE:
				Sound.A2_ENEMY_PLASMA_BOLT_FIRE.play();
				break;
			case A2_MYTHICITE_PICKUP:
				Sound.A2_Mythicite_Pickup.play();
				break;
			case A2_PLAYER_DESTROYED:
				Sound.A2_Player_Destroyed.play();
				break;
			case A2_PLAYER_HOMING_MISSILES_FIRED:
				Sound.A2_PLAYER_HOMING_MISSILES_FIRED.play();
				break;
			case A2_PLAYER_PLASMA_BOLT_FIRE:
				Sound.A2_PLAYER_PLASMA_BOLT_FIRE.play();
				break;
			case A2_PLAYER_PURCHASE_ACCEPTED:
				Sound.A2_Special_Power_Up_Pickup.play();
				break;
			case A2_PLAYER_PURCHASE_DENIED:
				Sound.A2_Purchase_Denied.play();
				break;
			case A2_PLAYER_SHIELD_IMPACT:
				Sound.A2_Player_Shield_Impact.playOnce();
				break;
			case A2_PLAYER_SONIC_DISRUPTOR_ACTIVE:
				Sound.A2_Player_Sonic_Disruptor_Active.playContinuously();
				break;
			case A2_PLAYER_SPAWNS:
				Sound.A2_PLAYER_SPAWNS.play();
				break;
			case A2_SPECIAL_POWERUP_PICKUP:
				Sound.A2_Special_Power_Up_Pickup.play();
				break;
			case A2_TROLL_APPEARS:
				Sound.A2_Troll_Appears.play();
				break;
			case A2_TROLL_DESTROYED:
				Sound.A2_Troll_Destroyed.play();
				break;
			case A2_TROLL_LASER_FIRED:
				Sound.A2_TROLL_LASER_FIRED.play();
				break;
			case A2_TROLL_SHIELD_IMPACT:
				Sound.A2_Troll_Shield_Impact.play();
				break;
				
			case A2_TROLL_MOTHERSHIP_APPEARS:
				Sound.A2_Troll_Mothership_Appears.play();
				break;
				
			case A2_VICTORY_FANFARE:
				Sound.A2_Victory_Fanfare.play();
				break;
			case A2_WARPING_OUT:
				Sound.A2_Warping_Out.play();
				break;
				
			default:
				//play no sound.
				break;
		} //end switch statement to play the correct sound based on what event has happened.
	} //end function playSoundEvent
	
	/**
	 * Stops a sound from playing once the moment has passed.
	 * @param theEvent 
	 */
	public void stopSoundEvent ( SoundManager.SOUND_EVENT theEvent )
	{
		switch ( theEvent )
		{
			case PLAYER_SHIELD_IMPACT:
				Sound.PLAYER_SHIELD_IMPACT.haltPlaying();
				break;
			
			case TRACTOR_BEAM_DEPLOYED:
				Sound.TRACTOR_BEAM_DEPLOYED.haltPlaying();
				break;
				
			case A2_PLAYER_SONIC_DISRUPTOR_ACTIVE:
				Sound.A2_Player_Sonic_Disruptor_Active.haltPlaying();
				break;
				
			default:
				//do nothing.
				break;
				
		} //end switch statement based on what event has ended.
	} //end function stopSoundEvent
	
	public void changeMusicSequence ( SOUNDTRACK_SEQUENCE chosenSequence )
	{
		//acquire the lock.
		soundLock.acquireUninterruptibly();
		
		//check and see if there really has been a change
		if ( chosenSequence == currentSoundTrack )
		{
			soundLock.release();
			return;					//no need to change. Exit now.
		}
		
		//otherwise, change the background music. set the new track to 0.
		currentSoundTrack = chosenSequence;
		trackNumber = 0;
		
		//finally, halt what is currently playing, so the manager starts playing the new track.
		haltAllMusic ();

		//release the lock.
		soundLock.release();
		
	} //end function changeMusicSequence
    
    /*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
    /**
	 * The entry point of the thread execution runs indefinitely, starting a song sequence playing
	 * when no music is currently playing.
	 * Changes in music tracks will trigger a silence, which causes this thread to start playing the next
	 * song in the new sequence.
	 */
	public void run()
	{
		//loops forever, but exits when the system does.
		while ( true )
		{
			//Check if a new song needs to be played.
			if (false == isSongPlaying() )
			{
				startSong();
			} 
			
			//Then sleep a while.
			//Must use a try block due to possible InterruptedException
			try
			{  Thread.sleep (GameConstants.THREAD_SLEEP_TIME); } //end try block to put thread to sleep.
			catch (InterruptedException e)
			{ //Don't care.
			} //end catch block to deal with InterruptedExceptions when sleeping.
		
		} //end while loop to periodically wake up and check if a new song needs to begin.
		
	} //end function run
	
	/**
	 * Is there currently a music track being played?
	 * @return Boolean value representing if there is a music track currently playing.
	 */
	private boolean isSongPlaying ()
	{
		for ( Sound currentSound: Sound.values() )
		{
			//If there is a sound that is both playing, and is a music track,
			//then return true.
			if ( currentSound.isMusicTrack() && currentSound.isPlaying() )
			{
				return true;
			} //end if to check for a music track playing.
		} //end for loop to iterate through music tracks, checking if they are playing.
		
		//if we get this far, then there are no music tracks playing. Return false.
		return false;
		
	} //end function isSongPlaying
	
	
	private void haltAllMusic ()
	{
		for ( Sound currentSound: Sound.values() )
		{
			if ( currentSound.isMusicTrack() && currentSound.isPlaying() )
			{
				currentSound.haltPlaying();
			} //end if to check for a music track playing.
		} //end for loop to iterate through music tracks, stopping them from playing.
	} //end function haltAllMusic
	
	/**
	 * Start the next appropriate song in the sequence, based on the sequence chosen and the track number.
	 */
	private void startSong( )
	{
		//acquire the lock.
		soundLock.acquireUninterruptibly();
		
		switch ( currentSoundTrack )
		{
			case ASTERAGE_1_TITLE_SCREEN:
				Sound.ASTERAGE_CLASSIC_MAIN_THEME.playContinuously();
				break;
				
			case ASTERAGE_1_GAME_OVER:
				Sound.ASTERAGE_CLASSIC_GAME_OVER_THEME.playContinuously();
				break;
				
			case ASTERAGE_1_HIGH_SCORE:
				Sound.ASTERAGE_CLASSIC_CELEBRATION.playContinuously();
				break;
				
			case ASTERAGE_2_TRACK1:
				Sound.A2_TRACK1_AsteRAGE_Begins.playContinuously();
				break;
				
			case ASTERAGE_2_TRACK2:
				Sound.A2_TRACK2_ASTRAL_JOURNEY.playContinuously();
				break;
				
			case ASTERAGE_2_TRACK3:
				Sound.A2_TRACK3_DESCENT.playContinuously();
				break;
				
			case ASTERAGE_2_TRACK4:
				Sound.A2_TRACK4_NOSTALGIA.playContinuously();
				break;
				
			case ASTERAGE_2_CELEBRATION:
				Sound.A2_TRACK5_SKYS_THE_LIMIT.playContinuously();
				break;
				
			case ASTERAGE_2_GAME_OVER:
				Sound.A2_TRACK6_SPACE_MINER_DIRGE.playContinuously();
				break;
			
			case NONE:
			default:
				//play no sound.
				break;
			
		} //end switch based on current sound track.
		
		//release the lock.
		soundLock.release();
	} //end function startSong
    
    /* **********************************************************************
				    Inner Classes
       ********************************************************************** */
	
	public static enum SOUND_EVENT
	{
		//Title screen events.
		TITLE_SCREEN_MENU_OPTION_CHANGED,
		TITLE_SCREEN_MENU_GAME_SELECTED,
		
		//Asterage 1 game events.
		PLAYER_FIRES_BULLET,
		ENEMY_FIRES_BULLET,
		ASTEROID_IMPACT,
		PLAYER_SHIELD_IMPACT,
		ENEMY_SHIELD_IMPACT,
		REWARD_EARNED,
		PLAYER_SHIP_APPEARS,
		ENEMY_APPEARS,
		BIG_ENEMY_APPEARS,
		PLAYER_DESTROYED,
		SMALL_ENEMY_DESTROYED,
		GIANT_LASER_FIRE,
		PLAYER_SHIP_DESTROYED,
		ENEMY_SHIP_DESTROYED,
		ALIEN_EMERGES,
		WEAPON_POD_DESTROYED,
		TRACTOR_BEAM_DEPLOYED,
		
		
		//ASTERAGE 2 game events;
		A2_ASTEROID_IMPACT,
		A2_ENEMY_PLASMA_BOLT_FIRE,
		A2_EXTRA_LIFE_AWARDED,
		A2_MYTHICITE_PICKUP,
		A2_PLAYER_DESTROYED,
		A2_PLAYER_HOMING_MISSILES_FIRED,
		A2_PLAYER_PLASMA_BOLT_FIRE,
		A2_PLAYER_PURCHASE_ACCEPTED,
		A2_PLAYER_PURCHASE_DENIED,
		A2_PLAYER_SHIELD_IMPACT,
		A2_PLAYER_SONIC_DISRUPTOR_ACTIVE,
		A2_PLAYER_SPAWNS,
		A2_SPECIAL_POWERUP_PICKUP,
		A2_TROLL_APPEARS,
		A2_TROLL_DESTROYED,
		A2_TROLL_LASER_FIRED,
		A2_TROLL_SHIELD_IMPACT,
		A2_TROLL_MOTHERSHIP_APPEARS,
		A2_VICTORY_FANFARE,
		A2_WARPING_OUT;
		
	} //end enumeration SoundEvent definition
	
	/**
	 * SOUNDTRACK_SEQUENCE inherently keeps track of which set of songs is being played.
	 * It also has a property of how many songs are in the sound track, so we can increment the count when a song finishes,
	 * and roll over to 0 when we reach the end of the sequence.
	 */
	public enum SOUNDTRACK_SEQUENCE
	{
		NONE ( 1 ),								//no music at all
		
		//Asterage 1 SoundTracks.
		ASTERAGE_1_TITLE_SCREEN ( 1 ),
		ASTERAGE_1_GAME_OVER ( 1 ),
		ASTERAGE_1_HIGH_SCORE( 1 ),
		
		ASTERAGE_2_TRACK1 ( 1 ),
		ASTERAGE_2_TRACK2 ( 1 ),
		ASTERAGE_2_TRACK3 ( 1 ),
		ASTERAGE_2_TRACK4 ( 1 ),
		ASTERAGE_2_CELEBRATION ( 1 ),
		ASTERAGE_2_GAME_OVER ( 1 ),
		ASTERAGE_2_VICTORY_FANFARE ( 1 );
		
		
		private int trackCount;
		
		SOUNDTRACK_SEQUENCE ( int passedTrackCount )
		{
			this.trackCount = passedTrackCount;
		} //end function 
		
		public int getTrackCount ()
		{
			return trackCount;
		} //end function 
	} //end enumerated Soundtrack sequence definition
}
