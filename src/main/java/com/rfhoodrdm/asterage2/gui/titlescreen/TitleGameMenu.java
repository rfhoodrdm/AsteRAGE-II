

package com.rfhoodrdm.asterage2.gui.titlescreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.TitleState;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TitleGameMenu
	extends PanelTemplate
	implements RequiresLoadedData {
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private static final long serialVersionUID = 7840000087939923937L;
	public static final int titleMenuWidth = 410;
	public static final int titleMenuHeight = 150;
	
	private JLabel asterage1MenuOption;
	private JLabel asterage2MenuOption;
	
	private JButton asterage1MenuSelectionIndicator;
	private JButton asterage2MenuSelectionIndicator;
	
	private final int menuOptionWidth = 260;
	private final int menuOptionHeight = 75;
	private final int menuIndicatorWidth = 50;
	private final int menuIndicatorHeight = 50;
	
	private final Dimension menuOptionDimension = new Dimension (menuOptionWidth, menuOptionHeight);
	private final Dimension menuSelectionIndicatorDimension = new Dimension ( menuIndicatorWidth, menuIndicatorHeight) ;
	
	private final int firstRowX = 25;
	private final int firstRowY = 20;
	private final int secondRowX = 100;
	private final int secondRowY = 80;
	
	private Color menuTextColor = new Color ( 255, 0, 0);
	private Font menuTextFont = new Font("Skia", Font.BOLD, 22);
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */

	public TitleGameMenu ()	{
		//set up the GUI components.
		this.setLayout ( null );		//honor the coordinates of sub components.
		int upperLeftX = (GUI.panelWidth/2) - ( titleMenuWidth / 2 );
		int upperLeftY = 500;
		this.setBounds ( upperLeftX, upperLeftY, titleMenuWidth, titleMenuHeight);
		
		asterage1MenuOption = new JLabel();
		asterage2MenuOption = new JLabel();
		asterage1MenuSelectionIndicator = new JButton();
		asterage2MenuSelectionIndicator = new JButton();
		asterage1MenuSelectionIndicator.setBorderPainted(false);
		asterage2MenuSelectionIndicator.setBorderPainted(false);
		asterage1MenuSelectionIndicator.setVisible(false);
		asterage2MenuSelectionIndicator.setVisible(false);
		asterage1MenuSelectionIndicator.setContentAreaFilled(false);
		asterage2MenuSelectionIndicator.setContentAreaFilled(false);
		
		asterage1MenuOption.setText("AsteRAGE Classic");
		asterage1MenuOption.setForeground(menuTextColor);
		asterage1MenuOption.setFont ( menuTextFont );
		asterage1MenuOption.setVerticalTextPosition(SwingConstants.TOP); 
		asterage2MenuOption.setText("AsteRAGE 2: to Infinity!");
		asterage2MenuOption.setForeground(menuTextColor);
		asterage2MenuOption.setFont ( menuTextFont );
		asterage2MenuOption.setVerticalTextPosition(SwingConstants.TOP); 
		asterage1MenuOption.setBounds( secondRowX, secondRowY, menuOptionWidth, menuOptionHeight );
		asterage2MenuOption.setBounds( secondRowX, firstRowY, menuOptionWidth, menuOptionHeight );
		
		asterage1MenuSelectionIndicator.setBounds( firstRowX, secondRowY, menuIndicatorWidth, menuIndicatorHeight );
		asterage2MenuSelectionIndicator.setBounds( firstRowX, firstRowY, menuIndicatorWidth, menuIndicatorHeight );
		asterage1MenuSelectionIndicator.setFocusable(false);	//can't receive focus
		asterage2MenuSelectionIndicator.setFocusable(false);	//can't receive focus.
		
		this.add(asterage1MenuOption);
		this.add(asterage2MenuOption);
		this.add(asterage1MenuSelectionIndicator);
		this.add(asterage2MenuSelectionIndicator);
		
	} 
	
	@Override
	public void loadRequiredData(DataLoader dataLoader) {
		asterage1MenuSelectionIndicator.setIcon(new ImageIcon (Image.GAME_SELECTOR_ICON_WHITE.getImage()) );
		asterage2MenuSelectionIndicator.setIcon(new ImageIcon (Image.GAME_SELECTOR_ICON_WHITE.getImage()) );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public void refreshTitleGameMenu(TitleState.GAME_SELECTION currentGameSelection) {
		//show and hide the appropriate buttons.
		switch ( currentGameSelection )	{
			case ASTERAGE1:
				asterage1MenuSelectionIndicator.setVisible( true );
				asterage2MenuSelectionIndicator.setVisible( false );
				break;
			case ASTERAGE2:
				asterage1MenuSelectionIndicator.setVisible( false );
				asterage2MenuSelectionIndicator.setVisible( true );
				break;
		} 
	}

	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
