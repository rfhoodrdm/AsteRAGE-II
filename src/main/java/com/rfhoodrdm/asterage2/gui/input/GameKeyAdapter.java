package com.rfhoodrdm.asterage2.gui.input;

import java.awt.event.KeyEvent;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.controller.Controller;

import lombok.extern.slf4j.Slf4j;

/**
 *	This class is responsible for gathering key strokes and key releases, and deliver them to the 
 * master controller class. The GameKeyAdapter is intended to be attached to the top level
 * game frame in the gui.
 */
@Component
@Slf4j
public class GameKeyAdapter
	extends java.awt.event.KeyAdapter
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	private final Controller controller;
	
	public GameKeyAdapter(Controller controller) {
		this.controller = Objects.requireNonNull(controller);
	} 

	@Override
	public void keyPressed(KeyEvent e)	{
	   int pressedKey = e.getKeyCode(); 
	   log.trace("Keypress registered: {}", pressedKey);
	   controller.processKeyEvent( pressedKey, GAME_KEY_EVENT.DOWN );    
	} 

	@Override
	public void keyReleased(KeyEvent e)	{
	    int releasedKey = e.getKeyCode();
	    
		//TODO convert keys to lower case, to avoid caps lock bug.
		log.trace("Keyrelease registered: {}", releasedKey);
	    controller.processKeyEvent( releasedKey, GAME_KEY_EVENT.UP );
	} 
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	/**
	 * Used to distinguish what types of key events are being delivered to the controller.
	 */
	public static enum GAME_KEY_EVENT {
		UP,
		DOWN;
	} 
} 