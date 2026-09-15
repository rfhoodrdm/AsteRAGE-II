package gameObjects.asterage1;
import java.awt.image.BufferedImage;
import gui.GUI;

/**
 * Mythicite is the objective of the Trololo's mission. Collect as much as possible!
 * @author roberthood
 */
public class Mythicite
extends SpaceObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	BufferedImage mythiciteSprite;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public Mythicite(double xSpawnLocation, double ySpawnLocation)
	{
		super ( xSpawnLocation, ySpawnLocation,
				50, 0, 0, 20,
				0, 25, 0);
		this.mythiciteSprite = GUI.Image.MYTHICITE.getImage();
	}
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public BufferedImage selectSprite()
	{
		return this.mythiciteSprite;
	} //end function selectSprite
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
