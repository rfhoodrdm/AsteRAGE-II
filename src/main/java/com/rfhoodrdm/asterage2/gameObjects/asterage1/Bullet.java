package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import com.rfhoodrdm.asterage2.gui.Image;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.LimitedLifespan;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a bullet projectile on the game board.
 */
public class Bullet
extends SpaceObject
implements LimitedLifespan
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final int bulletSpeed = 30;
	public static final int bulletMaxLifespan = 50;
	public static final int bulletDamage = 15;
	BULLET_OWNER bulletOwner;
	BufferedImage playerBulletImage;
	BufferedImage trollBulletImage;
	int currentLifespan;
	int maxLifespan;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Bullet ( double passedXPosition, double passedYPosition, int passedVelocityAngle, Bullet.BULLET_OWNER passedOwner)
	{
		//passed angleFacing for ship is converted to velocity angle for the bullet.
		super( passedXPosition, passedYPosition,
				15, passedVelocityAngle, 0,
				15, 25.0, 25.0, 0);
		
		this.bulletOwner = passedOwner;
		
		loadSprites();
		
		//set the life span to its maximum
		this.maxLifespan = bulletMaxLifespan;
		resetLifespan();
		
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	protected BufferedImage selectSprite ()
	{
		switch (this.bulletOwner)
		{
			case PLAYER:
				return playerBulletImage;
				
			case TROLL:
			default:	//needed for completions
				return trollBulletImage;
		} //end switch based on 
		
	} //end function selectSprite
	
	public Bullet.BULLET_OWNER getBulletOwner ()
	{
		return this.bulletOwner;
	} //end function getOwner
	
	/*		********************		LimitedLifespan Interface			******************	*/
	
	@Override
	public void resetLifespan ()
	{
		this.currentLifespan = this.maxLifespan;
	} //end function resetLifespan
	
	@Override
	public void ageObject ()
	{
		if ( this.currentLifespan > 0 )
		{
			this.currentLifespan -= 1;
		}
	} //end function ageObject
	
	@Override
	public boolean checkEndOfLifespan()
	{
		if ( this.currentLifespan <= 0 )
		{
			return true;
		}//end if
		
		//else
		return false;
	} //end function checkEndOfLifeSpan
	
	@Override
	public void endLifespan()
	{
		this.currentLifespan = 0;
	} //end function endLifespan
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		spaceObjectList.remove(this);
	} //end function killObject
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private void loadSprites()
	{
		this.playerBulletImage = Image.PLAYER_BULLET.getImage();
		this.trollBulletImage = Image.ENEMY_BULLET.getImage();
	} //end function loadSprites
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum BULLET_OWNER
	{
		PLAYER,
		TROLL;
	} //end enum BULLET OWNER definition
}
