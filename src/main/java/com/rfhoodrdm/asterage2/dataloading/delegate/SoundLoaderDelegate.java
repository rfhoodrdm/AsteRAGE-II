package com.rfhoodrdm.asterage2.dataloading.delegate;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.rfhoodrdm.asterage2.common.exceptions.DataLoaderException;
import com.rfhoodrdm.asterage2.utility.DebugManager;

/**
 * Delegate responsible for loading sound clips.
 */
public class SoundLoaderDelegate {

	private static final String SOUND_RESOURCE_DIRECTORY = "sounds";
	
	/**
	 * Loads one Clip into memory and returns it.
	 * Order of preference
	 * 	1) Sounds in /config/data folder. (custom overrides)  TODO!
	 *  2) Sounds from the resource loader, bundled with the game.
	 *  3) Default sound, if any. TODO!
	 * @throws DataLoaderException if no sound can be found in any location, including the default.
	 */
	
	public Clip loadSound(String soundName)
			throws DataLoaderException {
		
		Optional<Clip> soundMaybe = loadSoundWithResourceLoader(soundName);
		if(soundMaybe.isPresent()) {
			DebugManager.logMessage(5, "Successfully loaded from resource path: " + soundName);
			return soundMaybe.get();
		}
		
		throw new DataLoaderException (soundName);
	}

	private Optional<Clip> loadSoundWithResourceLoader(String soundName) {
		String soundPath = SOUND_RESOURCE_DIRECTORY + "/" + soundName;
		InputStream inputStream = this.getClass()
				.getClassLoader()
				.getResourceAsStream(soundPath);
		
		if (inputStream == null) {
			return Optional.empty();
		}
		
		try ( InputStream rawStream = inputStream; 
			  BufferedInputStream bufferedInputStream = new BufferedInputStream(rawStream); 
			  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream) ) {
				Clip loadedSound = AudioSystem.getClip();
				loadedSound.open(audioInputStream);
				
				return Optional.ofNullable(loadedSound);
		} catch (Exception ex) {
			DebugManager.logMessage(4, "Could not load sound due to exception: " + soundName + " At Path: " + soundPath + 
					" Exception: " + ex.getClass() + " " + ex.getMessage());
		}
		
		return Optional.empty();
	}
	
}
