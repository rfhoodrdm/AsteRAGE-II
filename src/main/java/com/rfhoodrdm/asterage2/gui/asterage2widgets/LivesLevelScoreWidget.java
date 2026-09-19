

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import javax.swing.SwingConstants;
import com.rfhoodrdm.asterage2.state.Asterage2State;

public class LivesLevelScoreWidget
extends BaseAsterageWidget
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	HUDLabel levelDescriptionLabel;			//"Level:"
	HUDLabel levelCountLabel;				//what actual level we are on
	HUDLabel livesDescriptionLabel;			//"Lives:"
	HUDLabel livesCountLabel;				//how many actual lives are left.
	HUDLabel scoreDescriptionLabel;			//"Score:"
	HUDLabel scoreCountLabel;				//shows the actual game score
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public LivesLevelScoreWidget()
	{
		super();
		initializeSubComponents();
	} //end constructor
	
	@Override
	protected void initializeSubComponents()
	{
		levelDescriptionLabel = new HUDLabel	("Lvl:",			HUDLabel.HUDLabelType.STANDARD);
		levelCountLabel = new HUDLabel			("00",				HUDLabel.HUDLabelType.MONOSPACE);
		livesDescriptionLabel = new HUDLabel	("Ships:",			HUDLabel.HUDLabelType.STANDARD);
		livesCountLabel = new HUDLabel			("00",				HUDLabel.HUDLabelType.MONOSPACE);
		scoreDescriptionLabel = new HUDLabel	("Score:",			HUDLabel.HUDLabelType.STANDARD);
		scoreCountLabel = new HUDLabel			("000000000000",	HUDLabel.HUDLabelType.MONOSPACE);
		
		//set left horizontal text alignment for the actual number count labels, and make them show white text
		levelCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		levelCountLabel.setForeground(Color.WHITE);
		livesCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		livesCountLabel.setForeground(Color.WHITE);
		scoreCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scoreCountLabel.setForeground(Color.WHITE); 
		
		//set up the layout and add the components to the widget.
		GridBagLayout LLSLayout = new GridBagLayout();
		this.setLayout(LLSLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(2,10,2,10);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.gridy = 0;
		layoutInfo.weighty = 1;
		
		layoutInfo.gridx = 0;
		layoutInfo.weightx = 0;
		add ( levelDescriptionLabel, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.weightx = 0;
		add ( levelCountLabel, layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.weightx = 0;
		add ( livesDescriptionLabel, layoutInfo );
		
		layoutInfo.gridx = 3;
		layoutInfo.weightx = 0;
		add ( livesCountLabel, layoutInfo );
		
		layoutInfo.gridx = 4;
		layoutInfo.weightx = 0;
		add ( scoreDescriptionLabel, layoutInfo );
		
		layoutInfo.gridx = 5;
		layoutInfo.weightx = 1;
		add ( scoreCountLabel, layoutInfo );
		
//		GridLayout LLSLayout = new GridLayout(1, 6);
//		setLayout(LLSLayout);
//		
//		add( levelDescriptionLabel);
//		add( levelCountLabel);
//		add( livesDescriptionLabel);
//		add( livesCountLabel);
//		add( scoreDescriptionLabel);
//		add( scoreCountLabel);
		
	} //end method initializeSubComponents
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	/**
	 *
	 * @param asterage2State
	 */
	@Override
	public void updateDisplay( Asterage2State asterage2State )
	{
		int currentGameLevel = asterage2State.getGameLevel();
		int currentShipsRemaining = asterage2State.getRemainingShips();
		long currentScore = asterage2State.getScore();

		levelCountLabel.setText(""+ String.format("%02d", currentGameLevel) );
		livesCountLabel.setText(""+ String.format("%02d", currentShipsRemaining) );
		scoreCountLabel.setText(""+ String.format("%012d", currentScore));
	} //end method updateDisplay
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
} //end class LivesLevelScoreWidget
