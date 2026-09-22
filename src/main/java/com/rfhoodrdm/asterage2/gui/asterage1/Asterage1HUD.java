package com.rfhoodrdm.asterage2.gui.asterage1;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.gui.templates.IconButtonTemplate;
import com.rfhoodrdm.asterage2.gui.templates.PanelTemplate;
import com.rfhoodrdm.asterage2.state.Asterage1State;

import lombok.extern.slf4j.Slf4j;


/**
 * Displays the game stats for the player during a game of AsteRAGE classic.
 */
@Component
@Slf4j
public class Asterage1HUD
	extends PanelTemplate
	implements RequiresLoadedData {
	
	private static final long serialVersionUID = -1392377842957687827L;

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private final Asterage1State asterage1State;
	
	public final int hudWidth = 1200;
	public final int hudHeight = 100;
	
	private JLabel shieldLabel;
	private JLabel scoreLabel;
	private JLabel levelLabel;
	private JLabel mythiciteLabel;
	
	private JProgressBar shieldBar;
	private JProgressBar mythiciteBar;
	private JTextField scoreTextField;
	private JTextField levelTextField;
	private JTextField livesTextField;
	
	private ShipLivesIcon shipLivesIcon;
	
	private Color hudTextColor = new Color ( 255, 0, 0);
	private Font hudTextFont = new Font("Skia", Font.BOLD, 20);
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public Asterage1HUD(Asterage1State asterage1State) {
		//set the location of this component.
		this.setBounds (0, 590, 1200, 100 );
		shieldLabel = new JLabel("Shields:");
		scoreLabel = new JLabel("Score:");
		levelLabel = new JLabel ( "Level:");
		mythiciteLabel = new JLabel ( "Mythicite:");
		shieldBar = new JProgressBar();
		mythiciteBar = new JProgressBar ();
		scoreTextField = new JTextField();
		levelTextField = new JTextField();
		livesTextField = new JTextField();
		shipLivesIcon = new ShipLivesIcon();
		
		shieldLabel.setFont(hudTextFont);
		scoreLabel.setFont(hudTextFont);
		levelLabel.setFont(hudTextFont);
		mythiciteLabel.setFont(hudTextFont);
		shieldLabel.setForeground( hudTextColor );
		scoreLabel.setForeground( hudTextColor );
		levelLabel.setForeground( hudTextColor );
		mythiciteLabel.setForeground( hudTextColor );
		
		levelLabel.setBounds ( 50, 10, 75, 40);
		scoreLabel.setBounds ( 50, 50, 75, 40);
		shieldLabel.setBounds ( 650, 10, 100, 40);
		mythiciteLabel.setBounds ( 650, 50, 100, 40);
		
		shieldBar.setBounds ( 750, 10, 400, 40);
		mythiciteBar.setBounds ( 750, 50, 400, 40);
		shipLivesIcon.setBounds (450, 10, 40, 40);
		
		scoreTextField.setBounds ( 150, 50, 400, 40);
		scoreTextField.setEditable( false );
		levelTextField.setBounds ( 150, 10, 50, 40);
		levelTextField.setEditable( false );
		livesTextField.setBounds ( 500, 10, 50, 40);
		livesTextField.setEditable( false );
		
		this.add(shieldLabel);
		this.add(scoreLabel);
		this.add(levelLabel);
		this.add(mythiciteLabel);
		this.add(shieldBar);
		this.add(mythiciteBar);
		this.add(scoreTextField);
		this.add(levelTextField);
		this.add(livesTextField);
		this.add(shipLivesIcon);
		
		this.asterage1State = asterage1State;
	} 
	
	@Override
	public void loadRequiredData(DataLoader dataLoader) {
		shipLivesIcon.setIcon(new ImageIcon (Image.EXTRA_LIFE_ICON.getImage()));
	}

	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 * Update the game stats display panel.
	 */
	public void refreshStatDisplay()
	{
		this.livesTextField.setText( "" + asterage1State.getExtraLives() );
		this.scoreTextField.setText( "" + asterage1State.getScore() );
		this.levelTextField.setText( "" + asterage1State.getLevel() );
		this.shieldBar.setValue( asterage1State.getPlayerShields() );
		this.mythiciteBar.setValue( asterage1State.getPlayerMythicite() );
	}
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	class ShipLivesIcon extends IconButtonTemplate	{
		
		private static final long serialVersionUID = 8279452475872503820L;

		ShipLivesIcon () {
			super();
		} 
	} 

}
