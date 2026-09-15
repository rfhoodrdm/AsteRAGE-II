package gameObjects.asterage1;

import java.awt.image.BufferedImage;
import gui.GUI;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author roberthood
 */
public class PlayerPod
extends TrollPod
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	PlayerShip playerShip;
	BufferedImage playerPodSprite;
	PLAYER_POD_STATUS playerPodStatus;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public PlayerPod( TrollMothership passedMothership, int passedIndex, PlayerShip passedPlayerShip)
	{
		super ( passedMothership, passedIndex );
		
		//set local variables.
		this.playerShip = passedPlayerShip;
		this.playerPodSprite = GUI.Image.POD_SPRITE.getImage();
		this.rotationalSpeed = 21; //override standard rotation of super class. Spin wildly when spawned.
		this.setPosition(	playerShip.getXPosition(),
							playerShip.getYPosition() );
		this.playerPodStatus = PLAYER_POD_STATUS.BEING_TRACTORED;			//spawns in the grip of the tractor beam.
		
	} //end constructor
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override
	public BufferedImage selectSprite ()
	{
		return this.playerPodSprite;
	} //end function selectSprite
	
	/**
	 * Change the current status of the player pod.
	 * @param newStatus 
	 */
	public void changePodStatus ( PLAYER_POD_STATUS newStatus )
	{
		this.playerPodStatus = newStatus;
		
		//stop spinning wildly when we are deploying or operational.
		//This is so we can take advantage of troll pod's movement code.
		if ( newStatus != PLAYER_POD_STATUS.BEING_TRACTORED )
		{
			this.rotationalSpeed = 0;
		}
	}  //end function changePodStatus.
	
	/**
	 * Return the current player pod status.
	 * @return 
	 */
	public PLAYER_POD_STATUS getPlayerPodStatus()
	{
		return this.playerPodStatus;
	} 
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	@Override
	public void moveObject ()
	{
		double expectedXLocation;
		double expectedYLocation;
		
		//Move differently, depending on what state we are in.
		switch ( getPlayerPodStatus() )
		{
			case BEING_TRACTORED:
				//spin wildly, like other ships caught in certain tractor beams.
				this.angleFacing += this.rotationalSpeed;
				this.checkAndCorrectAngleFacing();
				expectedXLocation = trollMothership.getXPosition();
				expectedYLocation = trollMothership.getYPosition();
				moveToCorrectPosition(expectedXLocation, expectedYLocation);
				break;
				
			case DEPLOYING:
				expectedXLocation = trollMothership.getPodXPosition(index);
				expectedYLocation = trollMothership.getPodYPosition(index);
				moveToCorrectPosition(expectedXLocation, expectedYLocation);
				facePlayerShip();
				break;
				
			case OPERATIONAL:
				super.moveObject();
				facePlayerShip();
				break;
			
		} //end switch based on what status we're in.	
	} //end function moveObject
	
	/**
	 * Always face the player ship. The player pod does not have the tracking ability of the troll pod.
	 */
	private void facePlayerShip()
	{
			double dx = playerShip.getXPosition() - this.getXPosition();
			double dy = playerShip.getYPosition() - this.getYPosition();
			
			this.angleFacing = (int) Math.floor( Math.toDegrees( Math.atan2(dx, -1 * dy))); 
	} //end function facePlayerShip
	
	
	/**
	 * Move towards the deploy location. If we're within striking distance of both, then we change our status to operational.
	 */
	private void moveToCorrectPosition(double passedExpectedXLocation, double passedExpectedYLocation)
	{
		double howFarToMove = 3.5;
		//Get the x and y coordinates that the mothership would LIKE us to be at.
		
		double dx = Math.abs(passedExpectedXLocation - this.getXPosition() );
		double dy = Math.abs(passedExpectedYLocation - this.getYPosition() );
		
		if (	( dx < howFarToMove ) && 
				( dy < howFarToMove ) 
			)
		{
			if ( getPlayerPodStatus() == PLAYER_POD_STATUS.BEING_TRACTORED )
			{
				//start deploying.
				this.changePodStatus(PLAYER_POD_STATUS.DEPLOYING);
				this.setPosition(passedExpectedXLocation, passedExpectedYLocation);
				return;
			}
			else if ( getPlayerPodStatus() == PLAYER_POD_STATUS.DEPLOYING )
			{
				//We're here. Set the status to operational. Done.
				this.changePodStatus(PLAYER_POD_STATUS.OPERATIONAL);
				this.setPosition(passedExpectedXLocation, passedExpectedYLocation);
				return;
			} 
			
		} //end if check to move to operational status.
		
		//Adjust x position by the full amount, or jump into the location if we're close enough.
		if ( dx >= howFarToMove )
		{
			if ( passedExpectedXLocation > this.getXPosition() )
			{
				this.xPosition += howFarToMove;
			}
			else if (passedExpectedXLocation < this.getXPosition() )
			{
				this.xPosition -= howFarToMove;
			} 
		} //end if checks for x adjustment.
		else
		{
			this.xPosition = passedExpectedXLocation;
		} //end else block to handle if we are very close.
		
		//Adjust y position by the full amount, or jump into the location if we're close enough.
		if ( dy >= howFarToMove )
		{
			if ( passedExpectedYLocation > this.getYPosition() )
			{
				this.yPosition += howFarToMove;
			}
			else if (passedExpectedYLocation < this.getYPosition() )
			{
				this.yPosition -= howFarToMove;
			} 
		} //end if checks for x adjustment.
		else
		{
			this.yPosition = passedExpectedYLocation;
		}
		
	} //end function moveToDeploy
	
	/**
	 * Just shoots towards the player ship. No tracking ability like the trolls do.
	 * @param spaceObjectList 
	 */
	@Override
	public void fireBullet ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		Bullet newBullet = new Bullet ( this.xPosition, this.yPosition, this.angleFacing, Bullet.BULLET_OWNER.TROLL);
		spaceObjectList.add ( newBullet );
	} //end function checkBulletCoolDown
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum PLAYER_POD_STATUS
	{
		BEING_TRACTORED,
		DEPLOYING,
		OPERATIONAL;
	} //end enum player pod status definition.
}
