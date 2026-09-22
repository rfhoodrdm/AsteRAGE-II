package com.rfhoodrdm.asterage2.entry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.rfhoodrdm.asterage2.controller.GamePulse;
import com.rfhoodrdm.asterage2.dataloading.DataLoader;
import com.rfhoodrdm.asterage2.dataloading.RequiresLoadedData;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.sounds.SoundManager;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AsteRAGE 2 entry class.
 * Prepares all of the other classes and components as needed.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AsteRAGE2 implements ApplicationRunner
{
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	private final GamePulse gamePulse;
	private final DataLoader dataLoader;
	private final SoundManager soundManager;
	private final GUI gui;
	private final GameKeyAdapter gameKeyAdapter;
	
	private final List<RequiresLoadedData> requiresLoadedDataComponentList;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Beginning asset load.");
		dataLoader.startLoading();
		log.info("Asset load complete.");
		
		//Pass data loader to modules which need to reference retrieved information.
		log.info("Calling components to fetch required data from asset load:");
		for(RequiresLoadedData currentComponent: requiresLoadedDataComponentList) {
			log.info("Current component: {}", currentComponent.getClass().getCanonicalName());
			currentComponent.loadRequiredData(dataLoader);
		}
		
		gui.registerKeyListener(gameKeyAdapter);
		
		//set the threads to running. Let the game begin!
		log.info("Initiating game threads.");
		executorService.submit(gamePulse);
		executorService.submit(soundManager);
		
		log.info("Revealing the GUI!");
		gui.showInitialGUI();
	}

	@PreDestroy
	public void shutdown() {
		executorService.shutdownNow();
	}
} 
