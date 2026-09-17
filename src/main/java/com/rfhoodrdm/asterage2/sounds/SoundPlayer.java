package com.rfhoodrdm.asterage2.sounds;

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
		
		
    } //end enum Sound definition
	
	public enum SOUND_TYPE
	{
		SOUND_EFFECT,
		MUSIC_TRACK;
	} 
} //end class SoundPlayer definition
