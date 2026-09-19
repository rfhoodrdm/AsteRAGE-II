package com.rfhoodrdm.asterage2.entry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.controller.Controller;
import com.rfhoodrdm.asterage2.controller.GamePulse;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.State;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

/**
 * AsteRAGE 2 entry class.
 * Prepares all of the other classes and components as needed.
 */
@Component
@RequiredArgsConstructor
public class AsteRAGE2 implements ApplicationRunner
{
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	
	//Temporary fields so we can move towards
	//TODO: remove when wiring is completely done by Spring.
	private final Controller controller;
	private final GamePulse gamePulse;
	private final DataLoader dataLoader;
	private final SoundManager soundManager;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//load the assets.
		dataLoader.startLoading();
		
		//Begin loading and initializing the main components of AsteRAGE 2.
		//Pass data loader to modules which need to reference retrieved information.
		State state = new State( dataLoader );	
		GUI gui = new GUI(soundManager);
		
		//Set references to other components from here.
		controller.setGUI ( gui );
		controller.setState ( state );
		gui.setController( controller );
		gui.setState( state );
		gamePulse.setController( controller );
		gamePulse.setGui(gui);
		
		//set the threads to running. Let the game begin!
		executorService.submit(gamePulse);
		executorService.submit(soundManager);
	}

	@PreDestroy
	public void shutdown() {
		executorService.shutdownNow();
	}
} 
