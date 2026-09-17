/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gui;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.rfhoodrdm.asterage2.state.TitleState;
import com.rfhoodrdm.asterage2.state.Asterage1State;
import com.rfhoodrdm.asterage2.state.Asterage2State;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 *
 * @author roberthood
 */
public class SplashScreen
extends PanelTemplate
{
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	//reference to top level component.
	GUI gui;
	TitleState titleState;
	Asterage1State asterage1State;		//needed for high scores
	Asterage2State asterage2State;		//needed for high scores
	
	//references to images used.
	TitleGameMenu titleGameMenu;
	BufferedImage titleGraphic;
	
	//Story-displaying components.
	StoryPane storyPane;
	HighScorePanel highScorePanel;
	
	ConcurrentLinkedQueue<StarPoint> starPointList;
	public static final int NUMBER_STARS_IN_FIELD = 100;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SplashScreen ( GUI passedGUI )
	{
		super();
		
		//remember reference to top level component.
		this.gui = passedGUI;
		
		//remember references to images we use to paint.
		titleGraphic = Image.TITLE_GRAPHIC.getImage();
		
		//Create and attach the Text Pane for the story.
		storyPane = new StoryPane();
		this.add(storyPane);
		
		//create and attach the panel for the high scores display
		highScorePanel = new HighScorePanel();
		this.add(highScorePanel);
		
		//add the game menu
		titleGameMenu = new TitleGameMenu();
		this.add( titleGameMenu );
		
		//set size and layout
		this.setSize ( GUI.panelWidth, GUI.panelHeight );
		this.setLayout ( null );
		
		createNewStarPointList();
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Refresh the title game menu instead of repainting the whole screen. It saves computation power.
	 */
	public void refreshTitleGameMenu()
	{
		//Get the necessary information out of the state.
		TitleState.GAME_SELECTION currentGameSelection = titleState.getCurrentGameSelected();
		titleGameMenu.refreshTitleGameMenu(currentGameSelection);
	} //end function refreshTitleGameMenu
	
	public void setTitleState ( TitleState passedTitleState )
	{
		this.titleState = passedTitleState;
	} //end function setState
	
	public void setAsterage1State ( Asterage1State passedState )
	{
		this.asterage1State = passedState;
	} 
	
	public void setAsterage2State ( Asterage2State passedState )
	{
		this.asterage2State = passedState;
	} 
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/** 
	 * Override the paint method.
	 * Draw the title graphic to the screen whenever we need to repaint.
	 * @param g The graphics object being passed in as a parameter, referring to the drawing surface.
	 */
	@Override
	protected void paintComponent ( Graphics g )
	{
		//invoke super class's paintComponent to redraw the frame.
		//super.paintComponent(g); displayHighScore( g ); return;
		super.paintComponent(g);	//call to super's paint
		
		switch ( titleState.getCurrentActivity() )
		{
			case DISPLAYING_TITLE:
			default:
				displayTitleGraphic( g );
				break;
				
			case DISPLAYING_STORY:
				displayStory( g );
				break;
				
			case DISPLAYING_HIGH_SCORE:
				displayHighScore( g );
				break;
		} //end switch based on activity
		
	} //end paintCompnent
	
	private void paintStarField ( Graphics g )
	{
		//advance the stars by 1 unit of time, for however fast they are traveling.
		//draw them.
		moveStars();
		paintStars(g);
		replaceExpiredStars();
	} //end method paintStarField
	
	private void replaceExpiredStars()
	{
		ArrayList<StarPoint> newStarList = new ArrayList<>();	//list of new stars we are going to add
		
		for ( StarPoint currentStar: starPointList )
		{
			if ( currentStar.checkExpired() )
			{
				starPointList.remove(currentStar);
				StarPoint newStar = StarPoint.createNewEdgeStar();
				newStarList.add( newStar );
			} //end if check to spawn a replacement star
		} //end for loop iterating through stars.
		
		//add the new stars to the list of existing stars.
		starPointList.addAll(newStarList);
	} //end method replaceExpiredStars
	
	private void moveStars()
	{
		for ( StarPoint currentStar: starPointList )
		{
			currentStar.moveStar();
		} //end for loop iterating through stars in the list
	} //end method moveStars
	
	private void paintStars( Graphics g )
	{
		for ( StarPoint currentStar: starPointList )
		{
			currentStar.paintStar(g);
		} //end for loop iterating through stars in the list.
	} //end method paintStars
	
	private void createNewStarPointList()
	{
		starPointList = new ConcurrentLinkedQueue<>();
		for ( int count = 1;  count <= NUMBER_STARS_IN_FIELD;  ++count )
		{
			starPointList.add(StarPoint.createNewRandomStarPoint());
		} //end for loop creating new stars
	} //end method createNewStarPointList
	
	private void displayTitleGraphic( Graphics g )
	{
		//hide the editor panes containing the other content.
		storyPane.setVisible(false);
		highScorePanel.setVisible(false);
		
		paintStarField(g);							//paint the star field background
		g.drawImage(titleGraphic, 0, 0, null);		//draw the title graphic.
	} //end function displayTitleGraphic
	
	private void displayStory( Graphics g )
	{
		//hide the editor panes containing the other content.
		highScorePanel.setVisible(false);
		
//		//paint over the AsteRAGE logo.
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, 1200, 600);
		
		paintStarField(g);							//paint the star field background
		
		//show the story pane.
		storyPane.setVisible(true);
	}
	
	private void displayHighScore( Graphics g )
	{
		//hide the editor panes containing the other content.
		storyPane.setVisible(false);
		
//		//paint over the AsteRAGE logo.
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, 1200, 600);
		
		paintStarField(g);							//paint the star field background
		
		//show the high score panel
		highScorePanel.setVisible(true);
	}
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static class StarPoint 
	{
		private int xCoordinate;	//location
		private int yCoordinate;	
		
		private int speed;			//how fast is this going?
		
		private StarPoint( int passedXLocation, int passedYLocation)
		{
			xCoordinate = passedXLocation;
			yCoordinate = passedYLocation;
			speed = (int) Math.floor( Math.random() * 4) + 1;	//from 1 to 4.
		} //end constructor
		
		public static StarPoint createNewRandomStarPoint()
		{
			return new StarPoint(	(int) Math.floor( Math.random() * GUI.panelWidth ),
									(int) Math.floor ( Math.random() * GUI.panelHeight ) );
		} //end factory method creating a new random starpoint at a random location.
		
		public static StarPoint createNewEdgeStar()
		{
			//can spawn anywhere along top or right edge.
			int locationRange = GUI.panelWidth + GUI.panelHeight;
			int randomLocation = (int) Math.floor( Math.random() * locationRange );
			
			if ( randomLocation < GUI.panelWidth )
			{
				//then it's a top edge star.
				return new StarPoint( randomLocation, 0 );
			}
			else
			{
				//else it's a right edge star
				return new StarPoint ( GUI.panelWidth, randomLocation - GUI.panelWidth );
			}
		} //end method createNewEdgeStar
		
		public boolean checkExpired ()
		{
			//if we've traveled off of the right edge or bottom edge, then this star has expired.
			return (( xCoordinate < 0 ) || (yCoordinate > GUI.panelHeight));
		} //end method checkExpired
		
		public void paintStar( Graphics g )
		{
			g.setColor(Color.WHITE);
			g.drawLine(xCoordinate-1, yCoordinate , xCoordinate+1, yCoordinate);
			g.drawLine(xCoordinate, yCoordinate-1, xCoordinate, yCoordinate+1);
		} //end method paintStar
		
		public void moveStar()
		{
			xCoordinate -= speed;
			yCoordinate += speed;
		} //end method moveStar
		
	} //end class StarPoint definition
	
	/**
	 * Contains the gui components which display the AsteRAGE storyline/mission to the splash panel.
	 */
	private class StoryPane
	extends JTextPane
	{
		//constructor
		public StoryPane()
		{
			//initially invisible and uneditable.
			setVisible(false);
			setEditable(false);
			//setBackground( Color.black);
			setOpaque(false);
			setBounds(5, 5, 1190, 475);
			
			//set up the contents.
			StyledDocument storyDoc = this.getStyledDocument();		//get document (content) reference.
			
			SimpleAttributeSet storyHeaderStyle = new SimpleAttributeSet();
			StyleConstants.setAlignment(storyHeaderStyle, StyleConstants.ALIGN_CENTER);
			StyleConstants.setFontSize(storyHeaderStyle, 48);
			StyleConstants.setBold(storyHeaderStyle, true);
			StyleConstants.setForeground(storyHeaderStyle, new Color ( 0xFF, 0xBB, 0x00) );
			
			SimpleAttributeSet storyBodyStyle = new SimpleAttributeSet();
			StyleConstants.setAlignment(storyBodyStyle, StyleConstants.ALIGN_JUSTIFIED);
			StyleConstants.setFontSize(storyBodyStyle, 30);
			StyleConstants.setBold(storyBodyStyle, true);
			StyleConstants.setItalic(storyBodyStyle, true);
			StyleConstants.setForeground(storyBodyStyle, new Color ( 0xFF, 0xFF, 0x99) );
			
			String titleString = "The AsteRAGE Mission: \n\n";
			
			String bodyString = "You are the captain of the mining ship, Trololo. You have been sent " +
								"to a distant sector of the quadrant in search of the valuable--and coveted--mineral " +
								"known as Mythicite. \n\n" + 
								"Unfortunately, this region of space is also claimed by the Space Troll Empire: " +
								"a hostile species that will undoubtly attack you at every opportunity. " + 
								"Exercise the utmost of caution and collect as much Mythicite as you can. \n\n" + 
								"Good luck, captain!";
			try
			{
				storyDoc.setParagraphAttributes(0, titleString.length(), 
													storyHeaderStyle, false);
				storyDoc.insertString( storyDoc.getLength(), 
						titleString, 
						storyHeaderStyle );
				
				storyDoc.setParagraphAttributes(titleString.length(), titleString.length() + bodyString.length(), 
													storyBodyStyle, true);
				storyDoc.insertString( storyDoc.getLength(), 
						bodyString, 
						storyBodyStyle );
			} //end try
			catch (BadLocationException e)
			{
				setText("And so the legacy of AsteRAGE was committed to the neither void of troll space...");
			} //end catch
		} //end constructor
	} //end inner class StoryPane definition
	
	/**
	 * Contains the part of the gui which displays the high score to the screen on the splash panel.
	 */
	private class HighScorePanel
	extends JPanel
	{
		//Constructor
		public HighScorePanel ()
		{
			//initially invisible and uneditable.
			setVisible(false);
			//setBackground( Color.black);
			setOpaque(false);
			setBounds(5, 5, 1190, 475);
			this.setLayout(null);			// honor sub-component coordinates.
			
			JLabel titleLabel = new JLabel("AsteRAGE All-Stars:");
			titleLabel.setFont( new Font( GameConstants.gameFont, Font.BOLD, 48) );
			titleLabel.setBounds(5, 5, 1190, 50);
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			titleLabel.setForeground( new Color (0x66, 0x99, 0xFF ));
			add(titleLabel);
			
			JLabel asterage1Label = new JLabel("AsteRAGE Classic");
			asterage1Label.setFont( new Font( GameConstants.gameFont, Font.BOLD, 24) );
			asterage1Label.setBounds(5, 65, 595, 25);
			asterage1Label.setHorizontalAlignment(SwingConstants.CENTER);
			asterage1Label.setForeground( new Color (0x66, 0x99, 0xFF ));
			add(asterage1Label);
			
			JLabel asterage2Label = new JLabel("AsteRAGE 2: to Infinity!");
			asterage2Label.setFont( new Font( GameConstants.gameFont, Font.BOLD, 24) );
			asterage2Label.setBounds(605, 65, 595, 25);
			asterage2Label.setHorizontalAlignment(SwingConstants.CENTER);
			asterage2Label.setForeground( new Color (0x66, 0x99, 0xFF));
			add(asterage2Label);
			
			//Make the high scores table.
			TableModel highScoresModel = new AbstractTableModel() 
			{
				@Override
				public int getColumnCount () {return 7; }
				
				@Override
				public int getRowCount () {return 10; }
				
				/**
				 * Get the relevant piece of information from the high score state objects.
				 * @param row	Represents which place (1st through 10th) being referenced in this call.
				 * @param column	Represents the type of information being retrieved.
				 * @return Object representing the high score entry's piece of information.
				 */
				@Override
				public Object getValueAt( int row, int column) 
				{ 
					switch ( column )
					{
						case 0:		//Name from Asterage 1.
							return asterage1State.getHighScoreNameAtPlace( row );
							
						case 1:		//Level achieved from Asterage 1.
							return asterage1State.getHighScoreLevelAtPlace( row );
							
						case 2:		//Points achieved from Asterage 1.
							return asterage1State.getHighScorePointsAtPlace( row );
							
						
						case 4:		//Name from Asterage 2
							return asterage2State.getHighScoreNameAtPlace( row );
							
						case 5:		//Level achieved from Asterage 2.
							return asterage2State.getHighScoreLevelAtPlace( row );
							
						case 6:		//Points Achieved from Asterage 2
							return asterage2State.getHighScorePointsAtPlace( row );
							
							
						default:
						case 3:		//spacer column between high score states.
							return "";
							
							
					} //end switch based on which column
				} //end getValueAt definition
			}; //end highScoresModel definition
			
			JTable highScoresTable = new JTable( highScoresModel );
			highScoresTable.setBounds(50, 100, 1100, 375);
			highScoresTable.setBackground( Color.black );
			highScoresTable.setOpaque(false);
			highScoresTable.setGridColor(Color.black);
			highScoresTable.setForeground(Color.white);
			highScoresTable.setRowHeight(35);
			highScoresTable.setFont( new Font ( "Skia", Font.BOLD, 20) );
			
			
			//fill the table
			for ( int row = 0; row < 10; ++row )
			{
				for ( int column = 0; column < 7; ++column)
				{
					highScoresTable.setValueAt(  Integer.toString(row * column) , row, column);
				} //end inner for loop
			} //end outer for loop
			
			add(highScoresTable );
		} //end constructor
	} //end inner class HighScorePanel definition
} // end class SplashScreen definition
