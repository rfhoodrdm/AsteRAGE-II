package com.rfhoodrdm.asterage2.sounds;

import javax.sound.sampled.Clip;
import lombok.Getter;
import lombok.Setter;

public enum Sound {

	// FOR TITLE SCREEN.
	DEFAULT("DEFAULT.aif", SoundType.SOUND_EFFECT), GAME_SELECTED("GAME_SELECTED.aif", SoundType.SOUND_EFFECT),
	MENU_OPTION_CHANGED("MENU_OPTION_CHANGED.aif", SoundType.SOUND_EFFECT),

	// FOR ASTERAGE 1 GAME SCREEN
	// Sound effects
	ENEMY_APPEARS("ENEMY_APPEARS.aif", SoundType.SOUND_EFFECT),
	BIG_ENEMY_APPEARS("BIG_ENEMY_APPEARS.aif", SoundType.SOUND_EFFECT),
	PLAYER_BULLET_FIRE("PLAYER_BULLET_FIRE.aif", SoundType.SOUND_EFFECT),
	ENEMY_BULLET_FIRE("ENEMY_BULLET_FIRE.aif", SoundType.SOUND_EFFECT),
	CREW_SCREAMS("CREW_SCREAMS.aif", SoundType.SOUND_EFFECT),
	PLAYER_SHIELD_IMPACT("PLAYER_SHIELD_IMPACT.aif", SoundType.SOUND_EFFECT),
	ENEMY_SHIELD_IMPACT("ENEMY_SHIELD_IMPACT.aif", SoundType.SOUND_EFFECT),
	ASTEROID_IMPACT("ASTEROID_IMPACT.aif", SoundType.SOUND_EFFECT),
	// ENGINE_THRUST("ENGINE_THRUST.aif", SoundType.SOUND_EFFECT),
	ENEMY_SHIP_DESTROYED("ENEMY_SHIP_DESTROYED.aif", SoundType.SOUND_EFFECT),
	PLAYER_SHIP_DESTROYED("PLAYER_SHIP_DESTROYED.aif", SoundType.SOUND_EFFECT),
	GIANT_LASER_FIRE("GIANT_LASER_FIRE.aif", SoundType.SOUND_EFFECT),
	PLAYER_SHIP_APPEARS("PLAYER_SHIP_APPEARS.aif", SoundType.SOUND_EFFECT),
	ALIEN_EMERGES("ALIEN_EMERGES.aif", SoundType.SOUND_EFFECT),
	REWARD_EARNED("REWARD_EARNED.aif", SoundType.SOUND_EFFECT),
	TRACTOR_BEAM_DEPLOYED("TRACTOR_BEAM_DEPLOYED.aif", SoundType.SOUND_EFFECT),

	// music
	ASTERAGE_CLASSIC_MAIN_THEME("ASTERAGE_CLASSIC_MAIN_THEME.aif", SoundType.MUSIC_TRACK),
	ASTERAGE_CLASSIC_GAME_OVER_THEME("ASTERAGE_CLASSIC_GAME_OVER_THEME.aif", SoundType.MUSIC_TRACK),
	ASTERAGE_CLASSIC_CELEBRATION("AsteRAGE_CLASSIC_CELEBRATION.aif", SoundType.MUSIC_TRACK),

	// FOR ASTERAGE 2 GAME SCREEN
	A2_Asteroid_Impact("A2_Asteroid_Impact.aif", SoundType.SOUND_EFFECT),
	A2_ENEMY_PLASMA_BOLT_FIRE("A2_ENEMY_PLASMA_BOLT_FIRE.aif", SoundType.SOUND_EFFECT),
	A2_EXTRA_LIFE_AWARDED("A2_EXTRA_LIFE_AWARDED.aif", SoundType.SOUND_EFFECT),
	A2_Mythicite_Pickup("A2_Mythicite_Pickup.aif", SoundType.SOUND_EFFECT),
	A2_Player_Destroyed("A2_Player_Destroyed.aif", SoundType.SOUND_EFFECT),
	A2_PLAYER_HOMING_MISSILES_FIRED("A2_PLAYER_HOMING_MISSILES_FIRED.aif", SoundType.SOUND_EFFECT),
	A2_PLAYER_PLASMA_BOLT_FIRE("A2_PLAYER_PLASMA_BOLT_FIRE.aif", SoundType.SOUND_EFFECT),
	A2_Player_Shield_Impact("A2_Player_Shield_Impact.aif", SoundType.SOUND_EFFECT),
	A2_Player_Sonic_Disruptor_Active("A2_Player_Sonic_Disruptor_Active.aif", SoundType.SOUND_EFFECT),
	A2_PLAYER_SPAWNS("A2_PLAYER_SPAWNS.aif", SoundType.SOUND_EFFECT),
	A2_Purchase_Denied("A2_Purchase_Denied.aif", SoundType.SOUND_EFFECT),
	A2_Special_Power_Up_Pickup("A2_Special_Power_Up_Pickup.aif", SoundType.SOUND_EFFECT),
	A2_Troll_Appears("A2_Troll_Appears.aif", SoundType.SOUND_EFFECT),
	A2_Troll_Destroyed("A2_Troll_Destroyed.aif", SoundType.SOUND_EFFECT),
	A2_TROLL_LASER_FIRED("A2_TROLL_LASER_FIRED.aif", SoundType.SOUND_EFFECT),
	A2_Troll_Mothership_Appears("A2_Troll_Mothership_Appears.aif", SoundType.SOUND_EFFECT),
	A2_Troll_Shield_Impact("A2_Troll_Shield_Impact.aif", SoundType.SOUND_EFFECT),
	A2_Victory_Fanfare("A2_Victory_Fanfare.aif", SoundType.SOUND_EFFECT),
	A2_Warping_Out("A2_Warping_Out.aif", SoundType.SOUND_EFFECT),

	A2_TRACK1_AsteRAGE_Begins("A2_TRACK1_AsteRAGE_Begins.aif", SoundType.MUSIC_TRACK),
	A2_TRACK2_ASTRAL_JOURNEY("A2_TRACK2_ASTRAL_JOURNEY.aif", SoundType.MUSIC_TRACK),
	A2_TRACK3_DESCENT("A2_TRACK3_DESCENT.aif", SoundType.MUSIC_TRACK),
	A2_TRACK4_NOSTALGIA("A2_TRACK4_NOSTALGIA.aif", SoundType.MUSIC_TRACK),
	A2_TRACK5_SKYS_THE_LIMIT("A2_TRACK5_SKY'S_THE_LIMIT.aif", SoundType.MUSIC_TRACK),
	A2_TRACK6_SPACE_MINER_DIRGE("A2_TRACK6_SPACE_MINER_DIRGE.aif", SoundType.MUSIC_TRACK);

	@Getter	final private String filename;
	@Getter	@Setter	private Clip clip;
	final private SoundType soundType;

	Sound(String passedFilename, SoundType passedType) {
		this.filename = passedFilename;
		this.soundType = passedType;
	}

	/**
	 * Play the sound clip from the beginning, even if it is already playing.
	 */
	public void play() {
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
	}

	/**
	 * Plays the sound clip in a never-ending loop.
	 * If the sound is already playing, then it stops, rewinds to the beginning, and starts playing again.
	 */
	public void playContinuously() {
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);

	} 

	/**
	 * Starts a new playing of the sound clip assuming it is not already playing.
	 */
	public void playOnce() {
		if (false == clip.isRunning()) {
			clip.setFramePosition(0);
			clip.start();
		}
	} 

	public void haltPlaying() {
		if (clip.isRunning()) {
			clip.stop();
			clip.setFramePosition(0);
		}
	}

	public boolean isPlaying() {
		return clip.isRunning();
	}

	/**
	 * Is this sound designated a sound effect? (vs. a music track.)
	 */
	public boolean isSoundEffect() {
		return SoundType.SOUND_EFFECT.equals(this.soundType);
	}

	/**
	 * Is this sound designated a music track? (vs. a sound effect.)
	 */
	public boolean isMusicTrack() {
		return SoundType.MUSIC_TRACK.equals(this.soundType);
	}
}
