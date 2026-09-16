/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.dataloading;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.rfhoodrdm.asterage2.dataloading.delegate.ImageLoaderDelegate;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.SoundPlayer;
import com.rfhoodrdm.asterage2.state.HighScoreDirectory;
import com.rfhoodrdm.asterage2.state.HighScoreEntry;
import com.rfhoodrdm.asterage2.utility.DataLoaderException;
import com.rfhoodrdm.asterage2.utility.DebugManager;
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
	
	//GUI Component pieces
    JFrame loaderFrame;
    JPanel loaderPanel;
    JLabel loaderLabel;
    JProgressBar loaderProgressBar;
	
	//loading delegates
    
    private final ImageLoaderDelegate imageLoaderDelegate;
    
	//High Scores components.
	ArrayList<HighScoreEntry> asterage1HighScoreList;
	ArrayList<HighScoreEntry> asterage2HighScoreList;
    
    //how many things do we have to load, and how far do we have to go?
    //used to set the state of the progress bar.
    int totalDataToLoad = 0;
    int dataLoadsCompleted = 0;
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public DataLoader()
	{
		//create the loading delegates.
		//TODO: rewire for injection, eventually.
		imageLoaderDelegate = new ImageLoaderDelegate();
		
		//easy to remember and reference sizes.
		Dimension loaderSize = new Dimension ( 500, 200 );
		Dimension componentSize = new Dimension ( 500, 100 );

		//initialize the gui components.
		loaderFrame = new JFrame ("Loading " + GameConstants.GAME_NAME + " data..." );
		loaderFrame.setDefaultCloseOperation(  JFrame.DO_NOTHING_ON_CLOSE );
		loaderFrame.setLayout ( null );
		loaderFrame.setSize ( loaderSize );
		loaderFrame.setLocationRelativeTo( null );

		loaderPanel = new JPanel();
		loaderPanel.setSize ( loaderSize );
		loaderPanel.setMinimumSize( loaderSize );
		loaderPanel.setLayout ( new BorderLayout() );
		loaderPanel.setBackground(new java.awt.Color(0, 0, 0));

		loaderLabel = new JLabel();
		loaderLabel.setSize( componentSize );
		loaderLabel.setMinimumSize ( componentSize );
		loaderLabel.setPreferredSize ( componentSize );
		loaderLabel.setMaximumSize ( componentSize );
		loaderLabel.setText ( "Loading data...");
		loaderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		loaderLabel.setFont(new java.awt.Font("Courier New", 1, 20)); // NOI18N
		loaderLabel.setForeground(new java.awt.Color(255, 255, 0));

		loaderProgressBar = new JProgressBar();
		loaderProgressBar.setSize ( componentSize );
		loaderProgressBar.setMinimumSize ( componentSize );
		loaderProgressBar.setPreferredSize ( componentSize );
		loaderProgressBar.setMaximumSize ( componentSize );
		loaderProgressBar.setStringPainted ( true );
		

		//assemble everything.
		loaderFrame.add ( loaderPanel );
		loaderPanel.add ( loaderLabel );
		loaderPanel.add ( loaderProgressBar );

		//show the loader that we have made upon game loading.
		loaderFrame.setVisible( true );
		
	} //end constructor
	
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void startLoading()
	{
		loaderLabel.setText ( "Load begin.");
		//At the start of loading, wait a short time after showing the frame.
		loaderProgressBar.setValue(0);
		try
		{
			Thread.sleep(500);
		} //end try block.
		catch ( InterruptedException e)
		{
			//nothing to do.
		} //end catch 
		
		//calculate how many items we must load. Sum of the count of all images, sounds, or other data items.
		int soundCount = SoundPlayer.Sound.values().length;
		int imageCount = GUI.Image.values().length;
		int highScoreFileCount = 2;
		
		this.totalDataToLoad = imageCount + soundCount + highScoreFileCount;
		
		//begin loading the data
		//if we are unable to find a resource, show a dialog box informing of that fact, and exit the game.
		try
		{
			loadAllImages();
			loadAllSounds();
			loadHighScores();
		}
		catch ( DataLoaderException e )
		{
			//get the name of the file that caused the error, display the error message, then exit the program.
			String nameOfResource = e.getMessage();
			JOptionPane.showMessageDialog(null, 
											"Cannot find file: " + nameOfResource + "\nThe game cannot start without all resources present.\nAsteRAGE 2 will now exit.", 
											"Unable to load a required game resource", 
											JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} //end catch block to handle DataLoaderException
		
		
		//At the end of loading, wait a short time, then hide the frame.
		loaderLabel.setText ( "Load complete.");
		displayProgress ( );
		try
		{
			Thread.sleep(1000);
		} //end try block.
		catch ( InterruptedException e)
		{
			//nothing to do.
		} //end catch 
		loaderFrame.setVisible ( false );	//and we're done!
		
	} //end function startLoading
	

	public ArrayList<HighScoreEntry> getAsterage1HighScoreList( HighScoreDirectory.GAME_IDENTIFIER whichGame )
	{
		switch ( whichGame )
		{
			case ASTERAGE1:
				return this.asterage1HighScoreList;
			case ASTERAGE2:
				return this.asterage2HighScoreList;
			default: 
				return null;
		} //end switch
	} //end function getAsterage1HighScoreList

	
	/**
	 * Invoke the XML handler to save the high score list given, under the file name provided.
	 * @param highScoreListToSave
	 * @param fileNameToSave 
	 */
	public void saveHighScoreList ( ArrayList<HighScoreEntry> highScoreListToSave, String fileNameToSave )
	{
		String fullFileNameToSave = BASE_HIGH_SCORE_DIRECTORY + fileNameToSave;
		HighScoreXMLHandler.saveXML(highScoreListToSave, fullFileNameToSave);
	}  //end function saveHighScoreList
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private void loadHighScores()
	{
		//load score for AsteRAGE 1.
		String highScores1FileName = GameConstants.ASTERAGE_1_HIGH_SCORE_FILE_NAME;
		++dataLoadsCompleted;
		updateLabel ( highScores1FileName );
		this.asterage1HighScoreList = HighScoreXMLHandler.loadXML(BASE_HIGH_SCORE_DIRECTORY + highScores1FileName );

		//load scores for AsteRAGE 2.
		String highScores2FileName = GameConstants.ASTERAGE_2_HIGH_SCORE_FILE_NAME;
		++dataLoadsCompleted;
		updateLabel ( highScores2FileName );
		this.asterage2HighScoreList = HighScoreXMLHandler.loadXML( BASE_HIGH_SCORE_DIRECTORY + highScores2FileName );
		
	} //end function loadHighScores
	
	private void loadAllImages()
			throws DataLoaderException
	{ 
		for (GUI.Image currentImage: GUI.Image.values() )
		{
			String fileName = currentImage.getFilename();
			updateLabel ( fileName );
			displayProgress();
			
			BufferedImage newImage = imageLoaderDelegate.loadImage( fileName );
			currentImage.setImage(newImage);
			++dataLoadsCompleted;
		} //end for each loop to iterate through and load all images.

	} //end function loadAllImages definition
	
	private void loadAllSounds()
			throws DataLoaderException
	{
		for (SoundPlayer.Sound currentSound : SoundPlayer.Sound.values() )
		{
			//get the file name, load the sound, and store it in the enum.
			//if we find a null, it is a problem.
			String fileName = currentSound.getFilename();
			Clip newSound = this.loadSound( fileName );
			currentSound.setClip(newSound);
			++dataLoadsCompleted;
		} //end for each loop to iterate through and load all sounds
		
	} //end function loadAllSounds definition
	
	
	/**
     * Sets the progress bar in the loader frame to reflect the % of progress 
     * the loader has completed in the data load.
     */
    private void displayProgress ( )
    {
		int calculatedPercentage;	//what % of the load is finished?

		//calculate what percentage of data to display.
		if ( totalDataToLoad > 0 )
		{
		calculatedPercentage = 
			( 100 * dataLoadsCompleted ) / totalDataToLoad;
		}
		else
		{
			//else we have division by 0. Assume 100%.
			calculatedPercentage = 100;
		} //end else block to prevent division by 0.

		loaderProgressBar.setValue( calculatedPercentage );
		loaderProgressBar.setString( calculatedPercentage + "%" );
    } //end function displayProgress
	
	/**
     * Sets new label text on the loader screen.
     * @param newMessage The string message to be displayed.
     */
    private void updateLabel ( String newMessage )
    {
		loaderLabel.setText(newMessage);
    } //end function updatelabel
	
	
	 /**
     * Load one sound into memory and return it.
     * @param fileName String value for locating our sound on disk.
     * @return The sound clip that was loaded, or null if no sound could be loaded.
     */
    private Clip loadSound ( String fileName )
	throws DataLoaderException
    {
		//make a new return value. We can set the key immediately.
		Clip loadedSound = null;

		//update the gui items 
		updateLabel ( fileName );
		displayProgress();

		String ref = SOUND_ASSET_DIRECTORY + "/" + fileName;
		try
		{
			File soundfile = new File( ref );
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundfile);
			loadedSound = AudioSystem.getClip();
			loadedSound.open (audioInputStream);	    
		} //end try
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
			//handle IOException, UnsupportedAudioException, and LineUnavailableException the same way.
		{
			DebugManager.logMessage(4, "Unable to load sound " + fileName );
			throw new DataLoaderException ( fileName + "\n Location: " + ref);
		} //end catch 

		//return our finished new list item.
		return loadedSound;
    } //end function loadImage
	
} //end class dataloader.
