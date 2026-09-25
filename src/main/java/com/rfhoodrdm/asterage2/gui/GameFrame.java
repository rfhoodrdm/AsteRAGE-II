package com.rfhoodrdm.asterage2.gui;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.asterage1.Asterage1GameScreen;
import com.rfhoodrdm.asterage2.gui.asterage2.AsteRAGE2GameScreen;
import com.rfhoodrdm.asterage2.gui.titlescreen.SplashScreen;

import lombok.extern.slf4j.Slf4j;

/**
 * GameFrame is the main frame for the entire program gui. It is where the main panel is placed, which houses
 * the main title screen, and AsteRAGE 1 and AsteRAGE 2 game panels.
 */
@Component
@Slf4j
public class GameFrame
	extends JFrame {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private static final long serialVersionUID = 4308661087062412979L;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public GameFrame(SplashScreen splashScreen, Asterage1GameScreen asterage1GameScreen, AsteRAGE2GameScreen asterage2GameScreen)	{
		super ( GameConstants.GAME_NAME + " - " + 
				GameConstants.GAME_VERSION);				
		
		//set the parameters of this component.
		
		this.setSize( GUI.panelWidth , GUI.panelHeight + 50);	//use given component sizes. Add some pixels to accommodate menu bar size differences
		this.setLayout( new GridBagLayout() );					
		this.setLocationRelativeTo( null );						//center on screen
		setExtendedState(JFrame.MAXIMIZED_BOTH);				//full windowed screen mode.
		this.getContentPane().setBackground ( Color.black );	//solid black background
		this.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );	//exit game on closing of window.
		
		this.setIgnoreRepaint(false);
		
		add(splashScreen);
		add(asterage1GameScreen);
		add(asterage2GameScreen);
	} 

	public void registerKeyListener(KeyListener keyListener) {
		this.addKeyListener(keyListener);
	}
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
