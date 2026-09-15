/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.asterage2widgets;

import gameEffects.asterage2.HUDExplosionEffect;
import gameEffects.asterage2.ShipExplosionEffect;
import gui.GUI;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.ImageIcon;
import state.Asterage2State;

/**
 *
 * @author roberthood
 */
public class ShipPowerupStatusWidget
extends BaseAsterageWidget
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	PowerUpMenuIconWidget decelerationPowerup;		//does ship have advanced deceleration?
	PowerUpMenuIconWidget shieldGeneratorPowerup;	//what level of shield regeneration does this ship have?
	PowerUpMenuIconWidget multishopPowerup;			//what level of multishot does ship have.
	
	PowerUpMenuIconWidget gravityNetPowerup;		//does ship have gravity net?
	PowerUpMenuIconWidget homingMissilePowerup;		//does ship have homing missiles active?
	PowerUpMenuIconWidget sonicDisruptorPowerup;	//does ship have sonic disruptor equipped?
	
	ImageIcon homingMissileLevel0Icon;				//various stages of the homing missile upgrade
	ImageIcon homingMissileLevel1Icon;	
	ImageIcon homingMissileLevel2Icon;	
	
	ImageIcon multiShotLevel0Icon;					//various stages of multi shot powerup
	ImageIcon multiShotLevel1Icon;
	ImageIcon multiShotLevel2Icon;
	
	ImageIcon sonicDisruptorIcon;					//icons for the states of the sonic disruptor upgrade
	ImageIcon sonicDisruptorEnabledIcon;
	
	ImageIcon decelerationLevel0Icon;				//various stages of deceleration powerup
	ImageIcon decelerationLevel1Icon;
	ImageIcon decelerationLevel2Icon; 
	
	ImageIcon shieldGeneratorLevel0Icon;			//various stages of the shield generator powerup
	ImageIcon shieldGeneratorLevel1Icon;
	ImageIcon shieldGeneratorLevel2Icon;
	
	ImageIcon gravityNetIcon;						//icons for the states of the gravity net upgrade
	ImageIcon gravityNetEnabledIcon;
	
	
	ConcurrentLinkedQueue<HUDExplosionEffect> hudExplosionList;		//list of explosion effects currently being drawn.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public ShipPowerupStatusWidget()
	{
		super();					//call to super's constructor
		initializeSubComponents();	//initialize the pieces of this widget
	} //end constructor
	
	@Override
	protected void initializeSubComponents()
	{
		//define the icons first
		homingMissileLevel0Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_0.getImage() );			
		homingMissileLevel1Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_1.getImage() );		
		homingMissileLevel2Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_HOMING_MISSILE_LEVEL_2.getImage() );		

		multiShotLevel0Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_MULTISHOT_LEVEL_0.getImage() );					
		multiShotLevel1Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_MULTISHOT_LEVEL_1.getImage() );	
		multiShotLevel2Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_MULTISHOT_LEVEL_2.getImage() );	

		sonicDisruptorIcon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_SONIC_DISRUPTOR_DISABLED.getImage() );						
		sonicDisruptorEnabledIcon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_SONIC_DISRUPTOR_ENABLED.getImage() );	

		decelerationLevel0Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_DECELERATION_LEVEL_0.getImage() );					
		decelerationLevel1Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_DECELERATION_LEVEL_1.getImage() );	
		decelerationLevel2Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_DECELERATION_LEVEL_2.getImage() );	 

		shieldGeneratorLevel0Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_0.getImage() );			
		shieldGeneratorLevel1Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_1.getImage() );	
		shieldGeneratorLevel2Icon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_SHIELD_GENERATOR_LEVEL_2.getImage() );	

		gravityNetIcon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_GRAVITY_NET_DISABLED.getImage() );						
		gravityNetEnabledIcon = new ImageIcon( GUI.Image.A2_SYSTEM_ICON_GRAVITY_NET_ENABLED.getImage() );	
		
		//now define the widgets that will hold these icons
		homingMissilePowerup = new PowerUpMenuIconWidget( homingMissileLevel0Icon );
		multishopPowerup = new PowerUpMenuIconWidget( multiShotLevel0Icon );
		sonicDisruptorPowerup = new PowerUpMenuIconWidget( sonicDisruptorIcon );
		
		decelerationPowerup = new PowerUpMenuIconWidget( decelerationLevel0Icon );
		shieldGeneratorPowerup = new PowerUpMenuIconWidget( shieldGeneratorLevel0Icon );
		gravityNetPowerup = new PowerUpMenuIconWidget( gravityNetIcon );
		
		//set basic layout info, and common layout attributes.
		GridBagLayout powerupStatusLayout = new GridBagLayout();
		this.setLayout(powerupStatusLayout);
		GridBagConstraints layoutInfo = new GridBagConstraints();
		layoutInfo.insets = new Insets(0,5,0,5);
		layoutInfo.fill = GridBagConstraints.BOTH;
		layoutInfo.weightx = 0.1;
		layoutInfo.weighty = 0.1;
		
	
		//place the widgets
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 0;
		add ( homingMissilePowerup,layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.gridy = 0;
		add ( multishopPowerup,layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.gridy = 0;
		add ( sonicDisruptorPowerup,layoutInfo );
		
		layoutInfo.gridx = 0;
		layoutInfo.gridy = 1;
		add ( decelerationPowerup,layoutInfo );
		
		layoutInfo.gridx = 1;
		layoutInfo.gridy = 1;
		add ( shieldGeneratorPowerup,layoutInfo );
		
		layoutInfo.gridx = 2;
		layoutInfo.gridy = 1;
		add ( gravityNetPowerup,layoutInfo );
		
		
		//now make a clear list of hudExplosions.
		hudExplosionList = new ConcurrentLinkedQueue<>();
		
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
		//query the state object for the relevant stats and display the appropriate icon.
		int homingMissileSystemLevel = asterage2State.getHomingMissileLevel();
		int multiShotSystemLevel = asterage2State.getMultishotLevel();
		boolean sonicDisruptorEquipped = asterage2State.checkSonicDisruptorEquipped();
		int decelerationSystemLevel = asterage2State.getDecelerationLevel();
		int shieldGeneratorSystemLevel = asterage2State.getShieldGeneratorLevel();
		boolean gravityNetEquipped = asterage2State.checkGravityNetEquipped();
		
		//choose the icons from the stats.
		ImageIcon homingMissileIconToDisplay = getHomingMissileSystemIcon(homingMissileSystemLevel);
		ImageIcon multiShotIconToDisplay = getMultishotSystemIcon(multiShotSystemLevel);
		ImageIcon sonicDisruptorIconToDisplay = (sonicDisruptorEquipped) ?
				sonicDisruptorEnabledIcon : sonicDisruptorIcon;
		
		ImageIcon decelerationIconToDisplay = getDecelerationSystemIcon(decelerationSystemLevel);
		ImageIcon shieldGeneratorIconToDisplay = getShieldGeneratorSystemIcon(shieldGeneratorSystemLevel);
		ImageIcon gravityNetIconToDisplay = (gravityNetEquipped) ?
				gravityNetEnabledIcon : gravityNetIcon;
		
		
		//set the icons
		homingMissilePowerup.setIcon(homingMissileIconToDisplay);
		multishopPowerup.setIcon(multiShotIconToDisplay);
		sonicDisruptorPowerup.setIcon(sonicDisruptorIconToDisplay);
		
		decelerationPowerup.setIcon(decelerationIconToDisplay);
		shieldGeneratorPowerup.setIcon(shieldGeneratorIconToDisplay);
		gravityNetPowerup.setIcon(gravityNetIconToDisplay);
		
	} //end method updateDisplay
	
	
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent(g); //call to super's paintcomponent
			
		//for each explosion effect in our list, draw it. Then, age it 1 tick. Then, check if its expired. If so, remove it.
		for ( HUDExplosionEffect currentEffect: hudExplosionList )
		{
			currentEffect.paintObject(g);
			currentEffect.decrementExpiredCountdownTimer();
			if ( true == currentEffect.checkExpired() ) { hudExplosionList.remove(currentEffect); }
		} //end for loop iterating through 
	} //end void paintComponent
	
	
	public void addHUDExplosion( SystemExplosionLocations whichSystemLocation )
	{
		//calculate the location of the new explosion
		int xLocation = 0;
		int yLocation = 0;
		switch ( whichSystemLocation )
		{
			case HOMING_MISSILE:
				xLocation = homingMissilePowerup.getX() + (homingMissilePowerup.getWidth() / 2);
				yLocation = homingMissilePowerup.getY() + (homingMissilePowerup.getHeight() / 2);
				break;
				
			case MULTI_SHOT:
				xLocation = multishopPowerup.getX() + (multishopPowerup.getWidth() / 2);
				yLocation = multishopPowerup.getY() + (multishopPowerup.getHeight() / 2);
				break;
				
			case SONIC_DISRUPTOR:
				xLocation = sonicDisruptorPowerup.getX() + (sonicDisruptorPowerup.getWidth() / 2);
				yLocation = sonicDisruptorPowerup.getY() + (sonicDisruptorPowerup.getHeight() / 2);
				break;
				
			case DECELERATOR:
				xLocation = decelerationPowerup.getX() + (decelerationPowerup.getWidth() / 2);
				yLocation = decelerationPowerup.getY() + (decelerationPowerup.getHeight() / 2);
				break;
				
			case SHIELD_GENERATOR:
				xLocation = shieldGeneratorPowerup.getX() + (shieldGeneratorPowerup.getWidth() / 2);
				yLocation = shieldGeneratorPowerup.getY() + (shieldGeneratorPowerup.getHeight() / 2);
				break;
				
			case GRAVITY_NET:
				xLocation = gravityNetPowerup.getX() + (gravityNetPowerup.getWidth() / 2);
				yLocation = gravityNetPowerup.getY() + (gravityNetPowerup.getHeight() / 2);
				break;
		} //end switch based on system location.
		
		HUDExplosionEffect newEffect = new HUDExplosionEffect ( xLocation, yLocation, 30, 30 );
		hudExplosionList.add(newEffect);
	} //end method addHUDExplosion
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private ImageIcon getHomingMissileSystemIcon(int homingMissileSystemLevel)
	{
		//level 0 icon for 0 argument, and so forth. 
		if ( 2 == homingMissileSystemLevel )	{	return homingMissileLevel2Icon;	}
		else if ( 1 == homingMissileSystemLevel ) { return homingMissileLevel1Icon; }
		else return homingMissileLevel0Icon;
	} //end method getHomingMissileSystemIcon
	
	private ImageIcon getMultishotSystemIcon(int multiShotSystemLevel)
	{
		//level 0 icon for 0 argument, and so forth. 
		if ( 2 == multiShotSystemLevel )	{	return multiShotLevel2Icon;	}
		else if ( 1 == multiShotSystemLevel ) { return multiShotLevel1Icon; }
		else return multiShotLevel0Icon;
	} //end method getMultishotSystemIcon
	
	private ImageIcon getDecelerationSystemIcon(int decelerationSystemLevel)
	{
		//level 0 icon for 0 argument, and so forth. 
		if ( 2 == decelerationSystemLevel )	{	return decelerationLevel2Icon;	}
		else if ( 1 == decelerationSystemLevel ) { return decelerationLevel1Icon; }
		else return decelerationLevel0Icon;
	} //end method getDecelerationSystemIcon
	
	private ImageIcon getShieldGeneratorSystemIcon(int shieldGeneratorSystemLevel)
	{
		//level 0 icon for 0 argument, and so forth. 
		if ( 2 == shieldGeneratorSystemLevel )	{	return shieldGeneratorLevel2Icon;	}
		else if ( 1 == shieldGeneratorSystemLevel ) { return shieldGeneratorLevel1Icon; }
		else return shieldGeneratorLevel0Icon;
	} //end method getShieldGeneratorSystemIcon
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	/**
	 * Officially label the systems which may be disabled/degraded.
	 */
	public static enum SystemExplosionLocations
	{
		HOMING_MISSILE,
		MULTI_SHOT,
		SONIC_DISRUPTOR,
		DECELERATOR,
		SHIELD_GENERATOR,
		GRAVITY_NET;
	} //end enum SystemExplosionLocations definition
	
} //end class ShipPowerupStatusWidget definition
