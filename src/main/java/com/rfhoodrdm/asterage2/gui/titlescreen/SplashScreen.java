

package com.rfhoodrdm.asterage2.gui.titlescreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.HighScorePanel;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StarPoint;
import com.rfhoodrdm.asterage2.gui.titlescreen.components.StoryPane;
import com.rfhoodrdm.asterage2.state.title.TitleState;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SplashScreen
	extends PanelTemplate
	implements RequiresLoadedData {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private static final long serialVersionUID = -7903939981568796746L;

	private final TitleState titleState;
	private BufferedImage titleGraphic;
	
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
		storyPane.setBounds(5, 105, 1190, 575);	//TODO: eventually introduce a layout manager to place the components.
		this.add(highScorePanel);
		
		this.highScorePanel = highScorePanel;
		highScorePanel.setBounds(5, 105, 1190, 575); //TODO: eventually introduce a layout manager to place the components.
		this.add(titleGameMenu);
		
		//set size and layout
		Dimension panelSize = new Dimension(GameConstants.GAME_PANEL_WIDTH, GameConstants.GAME_PANEL_HEIGHT);
		this.setSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setMaximumSize(panelSize);
		this.setLayout ( null );
	} 
	
	@Override
	public void loadRequiredData(DataLoader dataLoader) {
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
		paintStars(g);

		switch ( titleState.getCurrentActivity() ) {
			case DISPLAYING_STORY -> 		displayStory(g);
			case DISPLAYING_HIGH_SCORE -> 	displayHighScore( g );
			case DISPLAYING_TITLE	->		displayTitleGraphic( g );
		} 
		
		Toolkit.getDefaultToolkit().sync();	//flush repaints, to cure stuttering.
	} 
	
	/**
	 * Refresh the title game menu instead of repainting the whole screen. It saves computation power.
	 */
	private void refreshTitleGameMenu()	{

		TitleState.GAME_SELECTION currentGameSelection = titleState.getCurrentGameSelected();
		titleGameMenu.refreshTitleGameMenu(currentGameSelection);
	}
	
	private void paintStars( Graphics g ) {
		for ( StarPoint currentStar: titleState.getStarPointList())		{
			currentStar.paintStar(g);
		}
	} 
	
	private void displayTitleGraphic( Graphics g )	{
		storyPane.setVisible(false); 		//hide the editor panes containing the other content.
		highScorePanel.setVisible(false);
								
		g.drawImage(titleGraphic, 0, 100, null);		//draw the title graphic.
	} 
	
	private void displayStory( Graphics g )	{
		highScorePanel.setVisible(false); 		//hide the editor panes containing the other content.							
		storyPane.setVisible(true);
	}
	
	private void displayHighScore( Graphics g )	{
		storyPane.setVisible(false); //hide the editor panes containing the other content.									
		highScorePanel.setVisible(true);
	}
}
