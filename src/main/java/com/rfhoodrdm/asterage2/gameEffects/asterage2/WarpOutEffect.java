/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameEffects.asterage2;

import gameObjects.asterage2.SpaceObject;
import gui.GUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import utility.GameConstants;

/**
 *
 * @author roberthood
 */
public class WarpOutEffect
extends SpaceEffect
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int MAX_EXPIRED_COOLDOWN = GameConstants.FRAMES_PER_SECOND  * 3 / 4;	//~1 seconds long.
	public static final int ROTATIONAL_VELOCITY = 520 / MAX_EXPIRED_COOLDOWN;					//spin twice 
	
	private WarpEffectSize warpEffectSize;		//what size is this warp effect?
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public WarpOutEffect( SpaceObject parentObject, WarpEffectSize pWarpEffectSize )
	{
		super( parentObject.getxCoordinate(), parentObject.getyCoordinate() );	//call to super with location.
		resetCoundownToMax();													//reset the timer for this effect to max, so that it lives.
		warpEffectSize = pWarpEffectSize;										//how big to make this warp effect?
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected int getMaxExpiredCowndown()		{	return MAX_EXPIRED_COOLDOWN;	}
	@Override	protected BufferedImage getSimpleSprite()	{	return GUI.Image.A2_WARP_OUT_GRAPHIC.getImage();	}	//custom drawing. No sprite to show.
	@Override	protected double getMaxVelocity()			{	return 0;	}	//does not move
	
	
	@Override
	public void paintObject ( Graphics g )
	{
		//convert to Graphics2D object for more drawing capabilities.
		Graphics2D g2d = (Graphics2D) g;
		
		//determine the shifting size of the graphic for this iteration. 
		//The whole graphic follows the ratio of a sine wave from 0 to PI.
		double countDownRatioRemaining = ( (double)MAX_EXPIRED_COOLDOWN - (double)getExpiredCountdownTimer() ) / (double)MAX_EXPIRED_COOLDOWN;
		double scaleCooefficient = Math.sin( Math.PI * countDownRatioRemaining) * warpEffectSize.getScaleCoefficient();
		scaleCooefficient = (scaleCooefficient > 0.0 ) ? scaleCooefficient : 0.001;		//dont go to 0.
		
		int graphicXLocation = getxCoordinateAsInt();
		int graphicYLocation = getyCoordinateAsInt();
		BufferedImage effectSprite = getSimpleSprite();
		
		
		double facingDirectionRadians = Math.toRadians(getFacingAngleDegrees());
		setFacingAngleDegrees( getFacingAngleDegrees() + ROTATIONAL_VELOCITY);		//rotate, since we don't normally try to move effects.
		
		//create and calibrate the Affine Tranforms used to scale and rotate the object, respectively.
		//Perform the operations and draw the resulting image.
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleCooefficient, scaleCooefficient);
		AffineTransformOp scaleOp = new AffineTransformOp( scaleTransform, AffineTransformOp.TYPE_BILINEAR );
		BufferedImage scaledSprite = scaleOp.filter(effectSprite, null);
		
		int rotationXAnchor = scaledSprite.getWidth() / 2;
		int rotationYAnchor = scaledSprite.getHeight() / 2;
		AffineTransform rotateTransform = AffineTransform.getRotateInstance(facingDirectionRadians, rotationXAnchor, rotationYAnchor);
		AffineTransformOp rotateOp = new AffineTransformOp( rotateTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
		
		BufferedImage rotatedSprite = rotateOp.filter( scaledSprite, null);
		int drawOffsetX = scaledSprite.getWidth() / 2;		//offset is from scaled sprite; Rotate auto-corrects for center.
		int drawOffsetY = scaledSprite.getHeight() / 2;		//offset is from scaled sprite; Rotate auto-corrects for center.
		
		g2d.drawImage(	rotatedSprite, 
						graphicXLocation - drawOffsetX, 
						graphicYLocation - drawOffsetY, 
						null);

	} //end method paintObject
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum WarpEffectSize
	{
		NORMAL(1.0),
		SMALL(0.33);
		
		private final double scaleCoefficient;
		
		WarpEffectSize( double pScaleCoefficient )
		{
			scaleCoefficient = pScaleCoefficient;
		} //end constructor
		
		public double getScaleCoefficient() { return this.scaleCoefficient; }
	} //end enum WarpEffectSize definition
	
} //end class WarpOutEffect definition
