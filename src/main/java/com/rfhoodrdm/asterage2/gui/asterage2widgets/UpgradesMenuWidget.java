

package com.rfhoodrdm.asterage2.gui.asterage2widgets;

import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.DECELERATION;
import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.EXTRA_POINTS;
import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.HOMING_MISSILE;
import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.MULTISHOT;
import static com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption.SHIELD_GENERATOR;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.state.Asterage2State;
import com.rfhoodrdm.asterage2.state.Asterage2State.PowerUpMenuOption;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UpgradesMenuWidget
	extends BaseAsterageWidget
	implements RequiresLoadedData {

	private static final long serialVersionUID = -1350672135335095017L;

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private HUDLabel powerUpMenuDescription;				//"Select:"
	
	private PowerUpMenuIconWidget decelerationPowerUp;		//power up to slow down the ship
	private PowerUpMenuIconWidget shieldGeneratorPowerUp;	//power up for increased shield regeneration
	private PowerUpMenuIconWidget homingMissileShotPowerUp;	//power up to shoot a guided missile at the aliens
	private PowerUpMenuIconWidget extraLifePowerUp;			//power up for an extra life.
	private PowerUpMenuIconWidget multiShotPowerUp;			//power up to shoot multiple shots at once.
	
	private ImageIcon decelerationOptionIcon; 
	private ImageIcon decerationOptionSelectedIcon;
	private ImageIcon shieldGeneratorOptionIcon;
	private ImageIcon shieldGeneratorOptionSelectedIcon;
	
	private ImageIcon homingMissileOptionIcon;
	private ImageIcon homingMissileOptionSelectedIcon;
	private ImageIcon extraLifeOptionIcon;
	private ImageIcon extraLifeOptionSelectedIcon;
	
	private ImageIcon multiShotOptionIcon;
	private ImageIcon multiShotOptionSelectedIcon;
	
	private boolean dataLoaded = false;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public UpgradesMenuWidget()	{
		super();		
	} 
	
	@Override
	public void loadRequiredData(DataLoader dataLoader) {
		decelerationOptionIcon = new ImageIcon( Image.A2_POWER_UP_ICON_DECELERATION.getImage() );
		decerationOptionSelectedIcon = new ImageIcon( Image.A2_POWER_UP_ICON_DECELERATION_SELECTED.getImage() );
		shieldGeneratorOptionIcon = new ImageIcon( Image.A2_POWER_UP_ICON_SHIELD_GENERATOR.getImage() );
		shieldGeneratorOptionSelectedIcon =  new ImageIcon( Image.A2_POWER_UP_ICON_SHIELD_GENERATOR_SELECTED.getImage() );
		
		homingMissileOptionIcon = new ImageIcon( Image.A2_POWER_UP_ICON_HOMING_MISSILE.getImage() );
		homingMissileOptionSelectedIcon = new ImageIcon( Image.A2_POWER_UP_ICON_HOMING_MISSILE_SELECTED.getImage() );
		extraLifeOptionIcon = new ImageIcon( Image.A2_POWER_UP_ICON_POINTS.getImage() );
		extraLifeOptionSelectedIcon = new ImageIcon( Image.A2_POWER_UP_ICON_POINTS_SELECTED.getImage() );

		multiShotOptionIcon = new ImageIcon( Image.A2_POWER_UP_ICON_MULTISHOT.getImage() );
		multiShotOptionSelectedIcon = new ImageIcon( Image.A2_POWER_UP_ICON_MULTISHOT_SELECTED.getImage() );
		
		powerUpMenuDescription = new HUDLabel("Select:", HUDLabel.HUDLabelType.STANDARD);
		
		decelerationPowerUp = new PowerUpMenuIconWidget( decelerationOptionIcon );
		shieldGeneratorPowerUp = new PowerUpMenuIconWidget( shieldGeneratorOptionIcon );
		homingMissileShotPowerUp = new PowerUpMenuIconWidget( homingMissileOptionIcon );
		extraLifePowerUp = new PowerUpMenuIconWidget( extraLifeOptionIcon );
		multiShotPowerUp = new PowerUpMenuIconWidget( multiShotOptionIcon );
		
		initializeSubComponents();	//initialize the pieces of this widget
		dataLoaded = true;
	}
	
	@Override
	protected void initializeSubComponents() {
		//set basic layout info, and common layout attributes.
		GridBagLayout upgradesMenuLayout = new GridBagLayout();
		this.setLayout(upgradesMenuLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(0,5,0,5);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.gridy = 0;
		layoutInfo.weighty = 0;
		
		layoutInfo.gridx = 0;
		layoutInfo.weightx = 0.0;
		layoutInfo.anchor = GridBagConstraints.LINE_START;
		add ( powerUpMenuDescription, layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( decelerationPowerUp, layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( homingMissileShotPowerUp, layoutInfo );
		
		layoutInfo.gridx = 3;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( shieldGeneratorPowerUp, layoutInfo );
	
		layoutInfo.gridx = 4;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( extraLifePowerUp, layoutInfo );
		
		layoutInfo.gridx = 5;
		layoutInfo.weightx = 0.1;
		layoutInfo.anchor = GridBagConstraints.CENTER;
		add ( multiShotPowerUp, layoutInfo );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 *
	 */
	@Override
	public void updateDisplay( Asterage2State asterage2State )	{
		
		if (dataLoaded == false) {
			return;	//don't try to paint if we don't have icons.
		}
		
		//get the currently selected powerup from the state object, and show the appropriate icons for all menu options.
		PowerUpMenuOption currentlySelectedOption = asterage2State.getCurrentSelectedPowerUpMenuOption();
		
		ImageIcon decelerationIconToDisplay = (currentlySelectedOption == DECELERATION )
						?	decerationOptionSelectedIcon : decelerationOptionIcon;
		decelerationPowerUp.setIcon(decelerationIconToDisplay);
		
		ImageIcon shieldGeneratorIconToDisplay = (currentlySelectedOption == SHIELD_GENERATOR )
						?	shieldGeneratorOptionSelectedIcon : shieldGeneratorOptionIcon;
		shieldGeneratorPowerUp.setIcon(shieldGeneratorIconToDisplay);
		
		ImageIcon homingMissileIconToDisplay = (currentlySelectedOption == HOMING_MISSILE )
						?	homingMissileOptionSelectedIcon : homingMissileOptionIcon;
		homingMissileShotPowerUp.setIcon(homingMissileIconToDisplay);
		
		ImageIcon extraLifeIconToDisplay = (currentlySelectedOption == EXTRA_POINTS )
						?	extraLifeOptionSelectedIcon : extraLifeOptionIcon;
		extraLifePowerUp.setIcon(extraLifeIconToDisplay);
		
		ImageIcon multiShotIconToDisplay = (currentlySelectedOption == MULTISHOT )
						?	multiShotOptionSelectedIcon : multiShotOptionIcon;
		multiShotPowerUp.setIcon(multiShotIconToDisplay);
		
	} 

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
} 
