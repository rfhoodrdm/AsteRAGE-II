package com.rfhoodrdm.asterage2.gameEffects.asterage1;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.rfhoodrdm.asterage2.utility.GameConstants;
import java.awt.Color;

/**
 *
 * @author roberthood
 */
public class TrollLaser
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	SpaceObject attacker;
	SpaceObject target;
	
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TrollLaser ( SpaceObject passedTarget, SpaceObject passedAttacker)
	{
		super ( 0.0, 
				0.0, 
				(int) Math.floor (GameConstants.FRAMES_PER_SECOND * 0.5), 
				null );
		this.attacker = passedAttacker;
		this.target = passedTarget;
	} 
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	/**
	 * Remove the object from the active list of space objects at the end of its life span.
	 * @param spaceObjectList 
	 */
	public void killObject ( ConcurrentLinkedQueue <SpaceObject> spaceObjectList )
	{
		spaceObjectList.remove ( this );
	} //end function killObject
	
	/**
	 * Paint the laser to the game board.
	 * @param g 
	 */
	@Override
	public void paintToBoard ( Graphics g )
	{
		//convert the graphics object to graphics2d so we can set stroke length.
		Graphics2D g2d = (Graphics2D) g;
		int xAttacker = (int) Math.floor(attacker.getXPosition() );
		int yAttacker = (int) Math.floor(attacker.getYPosition() );
		int xTarget = (int) Math.floor(target.getXPosition() );
		int yTarget = (int) Math.floor(target.getYPosition() );
		
		Color laserColor = new Color ( 225, 225, 225 );
		g2d.setColor(laserColor);
		g2d.setStroke( new BasicStroke ( (float) 10.0 ) );
		
		g2d.drawLine(xAttacker, yAttacker, xTarget, yTarget);
		
		
	} //end function paintToBoard
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}

