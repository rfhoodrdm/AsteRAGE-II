

package com.rfhoodrdm.asterage2.gui.titlescreen;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.HighScorePanel;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StarPoint;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StoryPane;
import com.rfhoodrdm.asterage2.state.TitleState;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SplashScreen
	extends PanelTemplate {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private static final long serialVersionUID = -7903939981568796746L;

	private final TitleState titleState;
	private BufferedImage titleGraphic;
	
	//Story-displaying components.
	private final TitleGameMenu titleGameMenu;
	private final StoryPane storyPane;
	private final HighScorePanel highScorePanel;

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SplashScreen(TitleState titleState, StoryPane storyPane, HighScorePanel highScorePanel, TitleGameMenu titleGameMenu) {
		super();

		this.titleState = titleState;
		
		this.titleGameMenu = titleGameMenu;
		this.add(storyPane);
		
		this.storyPane = storyPane;
		this.add(highScorePanel);
		
		this.highScorePanel = highScorePanel;
		this.add(titleGameMenu);
		
		//set size and layout
		this.setSize ( GUI.panelWidth, GUI.panelHeight );
		this.setLayout ( null );
	} 
	

	public void referenceDataPostLoad() {
		//remember references to images we use to paint.
		titleGraphic = Image.TITLE_GRAPHIC.getImage();
	}
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/** 
	 * Override the paint method.
	 * Draw the title graphic to the screen whenever we need to repaint.
	 */
	@Override
	protected void paintComponent ( Graphics g ) {

		super.paintComponent(g);	
		refreshTitleGameMenu();
		
		switch ( titleState.getCurrentActivity() ) {
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
		} 
	} 
	
	/**
	 * Refresh the title game menu instead of repainting the whole screen. It saves computation power.
	 */
	private void refreshTitleGameMenu()
	{
		//Get the necessary information out of the state.
		TitleState.GAME_SELECTION currentGameSelection = titleState.getCurrentGameSelected();
		titleGameMenu.refreshTitleGameMenu(currentGameSelection);
	}
	
	private void paintStarFieldBackground ( Graphics g ) {
		paintStars(g);
	} 
	
	private void paintStars( Graphics g ) {
		for ( StarPoint currentStar: titleState.getStarPointList())		{
			currentStar.paintStar(g);
		}
	} 
	
	private void displayTitleGraphic( Graphics g )	{
		storyPane.setVisible(false); 		//hide the editor panes containing the other content.
		highScorePanel.setVisible(false);
		
		paintStarFieldBackground(g);							
		g.drawImage(titleGraphic, 0, 0, null);		//draw the title graphic.
	} 
	
	private void displayStory( Graphics g )	{
		highScorePanel.setVisible(false); 		//hide the editor panes containing the other content.
		paintStarFieldBackground(g);							
		storyPane.setVisible(true);
	}
	
	private void displayHighScore( Graphics g )	{
		storyPane.setVisible(false); //hide the editor panes containing the other content.		
		paintStarFieldBackground(g);							
		highScorePanel.setVisible(true);
	}

}
