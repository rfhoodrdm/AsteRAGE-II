/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.dataloading;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.rfhoodrdm.asterage2.dataloading.delegate.HighScoreXMLHandler;
import com.rfhoodrdm.asterage2.dataloading.delegate.ImageLoaderDelegate;
import com.rfhoodrdm.asterage2.dataloading.delegate.SoundLoaderDelegate;
import com.rfhoodrdm.asterage2.dataloading.popup.DataLoaderPopup;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.sounds.Sound;
import com.rfhoodrdm.asterage2.state.HighScoreDirectory;
import com.rfhoodrdm.asterage2.state.HighScoreEntry;
import com.rfhoodrdm.asterage2.utility.DataLoaderException;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 *
 * @author roberthood
 */
public class DataLoader
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final String BASE_ASSET_DIRECTORY = "src/main/resources";
	public static final String IMAGE_ASSET_DIRECTORY = BASE_ASSET_DIRECTORY + "/images";
	public static final String SOUND_ASSET_DIRECTORY = BASE_ASSET_DIRECTORY + "/sounds";
	
	public static final String BASE_HIGH_SCORE_DIRECTORY = "config/temp/";
	
	//loading delegates
    private final SoundLoaderDelegate soundLoaderDelegate;
    private final ImageLoaderDelegate imageLoaderDelegate;
    
    private final DataLoaderPopup dataLoaderPopup;
    
	//High Scores components.
	private ArrayList<HighScoreEntry> asterage1HighScoreList;
	private ArrayList<HighScoreEntry> asterage2HighScoreList;

	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public DataLoader()
	{
		//create the loading delegates.
		//TODO: rewire for injection, eventually.
		imageLoaderDelegate = new ImageLoaderDelegate();
		soundLoaderDelegate = new SoundLoaderDelegate();
		dataLoaderPopup = new DataLoaderPopup();

		//show the loader that we have made upon game loading.
		dataLoaderPopup.setVisible( true );
	} 
	
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void startLoading() {
		try {
			Thread.sleep(500);
		} catch (Exception ex) { }

		try	{
			loadAllImages();
			loadAllSounds();
			loadHighScores();
		} catch ( DataLoaderException e ) {
			//if we are unable to find a resource, show a dialog box informing of that fact, and exit the game.
			//get the name of the file that caused the error, display the error message, then exit the program.
			String nameOfResource = e.getMessage();
			JOptionPane.showMessageDialog(null, 
											"Cannot find file: " + nameOfResource + "\nThe game cannot start without all resources present.\nAsteRAGE 2 will now exit.", 
											"Unable to load a required game resource", 
											JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} 
		
		dataLoaderPopup.updateLabel ( "Load complete.");
		dataLoaderPopup.calculateAndDisplayProgress ( );
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {}
		dataLoaderPopup.setVisible ( false );	
	} 
	

	public ArrayList<HighScoreEntry> getAsterage1HighScoreList( HighScoreDirectory.GAME_IDENTIFIER whichGame ) {
		switch ( whichGame )
		{
			case ASTERAGE1:
				return this.asterage1HighScoreList;
			case ASTERAGE2:
				return this.asterage2HighScoreList;
			default: 
				return null;
		} 
	} 

	
	/**
	 * Invoke the XML handler to save the high score list given, under the file name provided.
	 * @param highScoreListToSave
	 * @param fileNameToSave 
	 */
	public void saveHighScoreList ( ArrayList<HighScoreEntry> highScoreListToSave, String fileNameToSave ){
		String fullFileNameToSave = BASE_HIGH_SCORE_DIRECTORY + fileNameToSave;
		HighScoreXMLHandler.saveXML(highScoreListToSave, fullFileNameToSave);
	}  
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private void loadHighScores() {
		//load score for AsteRAGE 1.
		String highScores1FileName = GameConstants.ASTERAGE_1_HIGH_SCORE_FILE_NAME;
		dataLoaderPopup.incrementDataLoadsCompleted();
		dataLoaderPopup.updateLabel ( highScores1FileName );
		this.asterage1HighScoreList = HighScoreXMLHandler.loadXML(BASE_HIGH_SCORE_DIRECTORY + highScores1FileName );

		//load scores for AsteRAGE 2.
		String highScores2FileName = GameConstants.ASTERAGE_2_HIGH_SCORE_FILE_NAME;
		dataLoaderPopup.incrementDataLoadsCompleted();
		dataLoaderPopup.updateLabel ( highScores2FileName );
		this.asterage2HighScoreList = HighScoreXMLHandler.loadXML( BASE_HIGH_SCORE_DIRECTORY + highScores2FileName );
		
	} 
	
	private void loadAllImages() throws DataLoaderException {
		for (Image currentImage : Image.values()) {
			String fileName = currentImage.getFilename();
			dataLoaderPopup.updateLabel(fileName);
			dataLoaderPopup.calculateAndDisplayProgress();

			BufferedImage newImage = imageLoaderDelegate.loadImage(fileName);
			currentImage.setImage(newImage);
			dataLoaderPopup.incrementDataLoadsCompleted();
		}
	}
	
	private void loadAllSounds() throws DataLoaderException {
		for (Sound currentSound : Sound.values()) {
			// get the file name, load the sound, and store it in the enum.
			String fileName = currentSound.getFilename();
			dataLoaderPopup.updateLabel(fileName);
			dataLoaderPopup.calculateAndDisplayProgress();

			Clip newSound = soundLoaderDelegate.loadSound(fileName);
			currentSound.setClip(newSound);
			dataLoaderPopup.incrementDataLoadsCompleted();
		}
	}
	
} 
