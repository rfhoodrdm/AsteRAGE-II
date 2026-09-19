
package com.rfhoodrdm.asterage2.controller;

import java.awt.event.KeyEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rfhoodrdm.asterage2.common.constants.CurrentState;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.Alien;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.MessageText;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShieldRing;
import com.rfhoodrdm.asterage2.gameEffects.asterage1.ShipDebrisExplosion;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.Asteroid;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.Bullet;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.PlayerPod;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollMothership;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollPod;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.TrollScout;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.gui.input.GameKeyAdapter;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.DeploysShields;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.FiresBullets;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.FiresSuperLaser;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage1.LimitedLifespan;
import com.rfhoodrdm.asterage2.sounds.SoundEvent;
import com.rfhoodrdm.asterage2.sounds.SoundManager;
import com.rfhoodrdm.asterage2.state.Asterage1ScoreState;
import com.rfhoodrdm.asterage2.state.Asterage1State;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Asterage1Controller {
	/*
	 * ********************************************************************** Data
	 * Members ******************
	 */
	@Getter
	@Setter
	private GUI gui;

	@Getter
	@Setter
	private Controller controller;

	@Getter
	@Setter
	private Asterage1State asterage1State;

	private int specialConditionCheckCounter = 1;
	private int maxSpecialConditionCheckCounter = GameConstants.FRAMES_PER_SECOND; // 1 special update per second.
	
	private final SoundManager soundManager;

	/*
	 * **********************************************************************
	 ******************** Constructor ******************
	 */

	public Asterage1Controller(SoundManager soundManager) {
		this.soundManager = soundManager;
	}

	/*
	 * ********************************************************************** 
	 * Class Interface ******************
	 */

	public void updateAsterage1State() {
		// top level gui matters:

		// get the list of space objects currently in the game.
		ConcurrentLinkedQueue<SpaceObject> spaceObjectList = asterage1State.getSpaceObjectList();

		// get the current reference to the player ship.
		PlayerShip playerShip = asterage1State.getPlayerShip();

		// If the game is not paused:
		if (asterage1State.getGameStatus() != Asterage1State.GAME_STATUS.PAUSE) {
			// Update space objects states.
			moveObjects(spaceObjectList); // move objects into new positions
			fireBullets(spaceObjectList); // those that are waiting to fire bullets do so.
			fireSuperLaser(playerShip, spaceObjectList); // those waiting to fire the super laser do so, at the
															// playership.
			fireOrUpdateTractorBeam(playerShip, spaceObjectList); // those waiting to fire the tractor beam may do so.
			checkAndResolveCollisions(spaceObjectList); // check for collisions of concern.
			regenerateShields(spaceObjectList); // regenerate shields for those objects which have them
			ageObjects(spaceObjectList); // age objects. Expired objects die.

			if (timeToDoSpecialUpdate()) {
				doSpecialUpdate(playerShip, asterage1State, spaceObjectList);
			}
			incrementSpecialUpdateCounter();

		}
		// update the stats board to reflect the new values of the game state.
		gui.repaint();

		// redraw the screen.
		gui.repaint();
	}

	/**
	 * Do heavy duty update checking that should only be done once every second or
	 * so, to conserve power.
	 */
	private void doSpecialUpdate(PlayerShip playerShip, Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		checkForLevelPromotion(playerShip, spaceObjectList);
		checkForExtraLifeAward(playerShip, asterage1State, spaceObjectList);
		checkForGameOver(playerShip, asterage1State, spaceObjectList);
		checkForTrollAppearance(asterage1State, spaceObjectList);
		checkForSpecialMessage(playerShip, asterage1State, spaceObjectList);
	}

	/**
	 * Entry point of the logic to determine how to react to key presses.
	 */
	public void processKeyEvent(int keyCode, GameKeyAdapter.GAME_KEY_EVENT whichEvent) {
		// First, filter out keystrokes that are not used, so we don't try to process
		// them.
		if (false == checkIfKeyUsed(keyCode)) {
			return; // don't try to process it.
		}

		// Key up, or key down?
		switch (whichEvent) {
		case UP:
			processKeyUpEvent(keyCode);
			break;

		case DOWN:
			processKeyDownEvent(keyCode);
			break;

		default:
			// do nothing
			return;
		}
	}

	/*
	 * **********************************************************************
	 ******************** Functionality ******************
	 */
	private void processKeyUpEvent(int keyCode) {
		// get the ship object, for if we need to invoke its movement interface.
		PlayerShip playerShip = asterage1State.getPlayerShip();

		// For State: SHIP IN PLAY
		if (true) {
			switch (keyCode) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				playerShip.stopAccelerating();
				break;

			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				playerShip.stopBraking();
				break;

			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				playerShip.stopRotating();
				break;

			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				playerShip.stopRotating();
				break;

			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_K:
				playerShip.stopFiringWeapons();
				playerShip.finishBulletCoolDown(); // set to 0 so we can space-bar fire.

			default:
				// do nothing.
				break;
			}
		}
	}

	private void processKeyDownEvent(int keyCode) {
		// get the ship object, for if we need to invoke its movement interface.
		PlayerShip playerShip = asterage1State.getPlayerShip();

		// For State: SHIP IN PLAY
		if (true) {
			switch (keyCode) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				playerShip.accelerate();
				break;

			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				playerShip.brake();
				break;

			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				playerShip.rotateClockWise();
				break;

			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				playerShip.rotateCounterClockwise();
				break;

			case KeyEvent.VK_P:
				togglePause(asterage1State);
				break;

			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_K:
				if (PlayerShip.SHIP_STATUS.ALIVE == playerShip.getShipStatus()) {
					playerShip.startFiringWeapons();
				} // end if for alive ship.
				else if (PlayerShip.SHIP_STATUS.DESTROYED == playerShip.getShipStatus()) {
					requestRespawnPlayerShip();
				}
				break;

			case KeyEvent.VK_ENTER:
				if (Asterage1State.GAME_STATUS.GAME_OVER == asterage1State.getGameStatus()) {
					doGameReset();
				}
				break;

			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_BACK_SPACE:
				if (Asterage1State.GAME_STATUS.GAME_OVER == asterage1State.getGameStatus()) {
					returnToTitleScreen();
				}
				break;

			default:
				; // do nothing.
				break;

			}
		}
	}

	/**
	 * Check the keystroke against our list of used keys. If the key is used, return
	 * true. Else return false.
	 * 
	 */
	private boolean checkIfKeyUsed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_W:
		case KeyEvent.VK_S:
		case KeyEvent.VK_A:
		case KeyEvent.VK_D:
		case KeyEvent.VK_K:
		case KeyEvent.VK_P:
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			return true;

		default:
			return false;
		}
	}

	/**
	 * Iterate through the space object list, and move objects that do movement.
	 * 
	 * @param spaceObjectList
	 */
	private void moveObjects(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// each space object is movable, in its own way. Call the move function.
		for (SpaceObject currentObject : spaceObjectList) {
			currentObject.moveObject();
			// troll scouts may opt to change course.
			if ((currentObject instanceof TrollScout) && (Math.random() < 0.01)) {
				TrollScout trollScout = (TrollScout) currentObject;
				trollScout.plotNewCourse(asterage1State.getLevel()); // sent level as argument, which helps determine
																		// course.
			}
		}
	}

	/**
	 * Iterate through the list, and have objects that fire bullets do so, if they
	 * want to.
	 * 
	 * @param spaceObjectList
	 */
	private void fireBullets(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof FiresBullets) {
				FiresBullets bulletObject = (FiresBullets) currentObject;
				// check if we're ready to fire a bullet.
				if (true == bulletObject.checkBulletCoolDown()) {
					bulletObject.fireBullet(spaceObjectList); // fire a bullet
					bulletObject.restartBulletCoolDown(); // initiate bullet cooldown.
					playSoundForBullet(bulletObject); // which sound to play?
				} else {
					// if not, decrement the object's bullet countdown
					bulletObject.decrementBulletCoolDown();
				}
			}
		}
	}

	/**
	 * Those about to fire the super laser do so. Decrement the cooldown of those
	 * waiting.
	 * 
	 * @param List
	 */
	public void fireSuperLaser(PlayerShip playerShip, ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		if (asterage1State.getLevel() < TrollMothership.superLaserLevelAcquired) {
			return; // without firing
		} // end if block to check if game is of sufficient level for the troll super
			// laser to fire.
			// unlike bullets, if the player is dead, do not fire at the ship.
		if (PlayerShip.SHIP_STATUS.ALIVE != playerShip.getShipStatus()) {
			return;
		}

		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof FiresSuperLaser) {
				FiresSuperLaser laserObject = (FiresSuperLaser) currentObject;
				if (true == laserObject.checkLaserCooldown()) {
					laserObject.fireLaser(playerShip, spaceObjectList);
					laserObject.restartLaserCooldown();
					soundManager.playSoundEvent(SoundEvent.GIANT_LASER_FIRE);

					// apply the damage immediately.
					// player ship gets damaged.
					double amountDamage = TrollMothership.maxLaserCooldown * PlayerShip.shieldRegenerationRate;
					playerShip.damageShields(amountDamage, spaceObjectList);
					if (false == playerShip.hasShieldsAttached()) {
						ShieldRing shieldRing = new ShieldRing(playerShip);
						playerShip.attachShields(shieldRing);
						spaceObjectList.add(shieldRing);
					} else {
						playerShip.renewShieldEffect();
					}

				} else {
					laserObject.decrementLaserCooldown();
				}
			}
		}
	}

	/**
	 * For the tractor beam, we can cheat a little, since only the troll mothership
	 * fires this weapon.
	 * 
	 * @param playerShip
	 * @param spaceObjectList
	 */
	private void fireOrUpdateTractorBeam(PlayerShip playerShip, ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof TrollMothership) {
				TrollMothership trollMothership = (TrollMothership) currentObject;

				// Check to see if the tractor beam is ready to fire, and if the player has an
				// extra life to steal.
				if ((asterage1State.getLevel() >= TrollMothership.tractorBeamLevelAcquired)
						&& (asterage1State.getExtraLives() > 0) && (true == trollMothership.checkTractorReadiness())) {
					log.debug("Fire the tractor beam!");
					trollMothership.fireTractor(playerShip); // start firing at the player ship.
					trollMothership.restartTractorCooldown();
				} else {
					// if not, decrement the object's tractor beam countdown
					trollMothership.decrementTractorCoolDown();
				}

				// now, if the tractor beam is already firing, update it according to its
				// operation.
				TrollMothership.TRACTOR_BEAM_STATUS tractorStatus = trollMothership.getTractorStatus();
				if ((TrollMothership.TRACTOR_BEAM_STATUS.TARGETING == tractorStatus)
						|| (TrollMothership.TRACTOR_BEAM_STATUS.TRACTORING_TARGET == tractorStatus)) {
					updateTractorBeam(playerShip, trollMothership, spaceObjectList);
				}
			}
		}
	}

	/**
	 * Update the state of the tractor beam. Increase the range of a targetting
	 * beam, check to see if beam should be disengaged, and so on. Also play the
	 * tractor noise if appropriate.
	 * 
	 * @param trollMothership
	 */
	private void updateTractorBeam(PlayerShip playerShip, TrollMothership trollMothership,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// If the tractor beam is targetting, i.e. reaching out to the player:
		if (TrollMothership.TRACTOR_BEAM_STATUS.TARGETING == trollMothership.getTractorStatus()) {
			// play the tractor beam sound, since it's on.
			soundManager.playSoundEvent(SoundEvent.TRACTOR_BEAM_DEPLOYED);

			// If the player becomes not alive during the targeting time, switch the beam
			// off.
			if (PlayerShip.SHIP_STATUS.ALIVE != playerShip.getShipStatus()) {
				// turn it off and turn the sound off.
				trollMothership.disengageTractor();
				soundManager.stopSoundEvent(SoundEvent.TRACTOR_BEAM_DEPLOYED);
				return;
			}

			// Increment the tractor beam range.
			trollMothership.incrementTractorRange();

			// If the tractor beam has reached its target -- reach of tractor beam =
			// distance from attacker to target--
			// Signal a lock on.
			SpaceObject tractorTarget = trollMothership.getTractorTarget();
			double dx = tractorTarget.getXPosition() - trollMothership.getXPosition();
			double dy = tractorTarget.getYPosition() - trollMothership.getYPosition();
			double rangeToTarget = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

			if (rangeToTarget < trollMothership.getCurrentTractorRange()) {
				log.debug("Locked on!");
				trollMothership.lockOnTractorBeam(tractorTarget);
			}
		}

		// if the tractor beam has siezed on a target:
		if (TrollMothership.TRACTOR_BEAM_STATUS.TRACTORING_TARGET == trollMothership.getTractorStatus()) {
			// play the tractor beam sound, since it's on.
			soundManager.playSoundEvent(SoundEvent.TRACTOR_BEAM_DEPLOYED);

			SpaceObject tractorTarget = trollMothership.getTractorTarget();
			if (tractorTarget instanceof PlayerShip) {
				updateTractorAtPlayerShip((PlayerShip) tractorTarget, trollMothership, spaceObjectList);
			} else if (tractorTarget instanceof PlayerPod) {
				updateTractorAtPlayerPod((PlayerPod) tractorTarget, trollMothership);
			}
		}
	}

	/**
	 * Update the states involving a tractor beam when it has locked onto a player.
	 */
	private void updateTractorAtPlayerShip(PlayerShip playerShip, TrollMothership trollMothership,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// decrement the player ship's hull stress by 1.
		playerShip.decrementTractorHullStress();

		// If the player's hull has reached its limit, or else the player has died, then
		// spawn the player pod.
		if ((playerShip.checkTractorHullStressLimit())
				|| (PlayerShip.SHIP_STATUS.ALIVE != playerShip.getShipStatus())) {
			int index = trollMothership.getUnusedPodIndex(); // what index to add at?
			PlayerPod newPlayerPod = new PlayerPod(trollMothership, index, playerShip); // make the pod
			asterage1State.decrementExtraLives(); // steal a life.
			trollMothership.lockOnTractorBeam(newPlayerPod); // change the tractor target
			trollMothership.attachTrollPod(newPlayerPod, index); // officially attach the pod.
			spaceObjectList.add(newPlayerPod); // add the pod to the list of objects.
			ShipDebrisExplosion newExplosion = // add an explosion effect.
					new ShipDebrisExplosion(playerShip.getXPosition(), playerShip.getYPosition(),
							ShipDebrisExplosion.DebrisExplosionOwner.PLAYER);
			spaceObjectList.add(newExplosion);
			soundManager.playSoundEvent(SoundEvent.PLAYER_DESTROYED); // imposing sound effect

			playerShip.resetTractorHullStress(); // reset hull stress back to max.

			log.debug("Spawning and attaching new player pod with index of {}", index);
		}
	}

	/**
	 * Update the states involving a tractor beam when it has locked onto a player
	 * pod.
	 */
	private void updateTractorAtPlayerPod(PlayerPod playerPod, TrollMothership trollMothership) {
		// The troll pod mostly takes care of maneuvering itself.
		// Once the player pod has been dragged to the troll ship, switch the tractor
		// beam off.
		if (playerPod.getPlayerPodStatus() != PlayerPod.PLAYER_POD_STATUS.BEING_TRACTORED) {
			trollMothership.disengageTractor(); // turn off the beam.
			soundManager.stopSoundEvent(SoundEvent.TRACTOR_BEAM_DEPLOYED); // stop playing the sound.
		}
	}

	private void playSoundForBullet(FiresBullets bulletObject) {
		if (bulletObject instanceof PlayerShip) {
			soundManager.playSoundEvent(SoundEvent.PLAYER_FIRES_BULLET);
		} else if (bulletObject instanceof TrollScout) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_FIRES_BULLET);
		} else if (bulletObject instanceof TrollMothership) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_FIRES_BULLET);
		} else if (bulletObject instanceof TrollPod) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_FIRES_BULLET);
		}
	}

	/**
	 * Handle collisions between objects. Of note: PlayerShip and asteroids. Bullets
	 * and asteroids. Bullets and PlayerShip -- for troll bullets. Bullets and
	 * TrollShips -- for player bullets.
	 * 
	 * @param spaceObjectList
	 */
	private void checkAndResolveCollisions(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// N^2 complexity, unfortunately, due to no specific get-at-index function for
		// ConcurrentLinkedQueue.
		// *** MUST ensure that collision checks are asymmetrical, otherwise we will
		// count some twice.
		// e.g. Once when the player ship is checked against an asteroid, and again when
		// the asteroid is checked against the ship.

		outerLoop: for (SpaceObject firstObject : spaceObjectList) {
			for (SpaceObject secondObject : spaceObjectList) {
				// start going through the pairs of objects of concern.
				// bullet to asteroid
				if ((firstObject instanceof Asteroid) && (secondObject instanceof Bullet)
						&& (firstObject.checkForCollision(secondObject))) {
					resolveBulletToAsteroid((Asteroid) firstObject, (Bullet) secondObject, spaceObjectList);
					continue outerLoop; // get a new set of objects, since both will die. Fixes the mothership
										// shattering effect.
				}

				// player ship to asteroid.
				if ((firstObject instanceof PlayerShip) && (secondObject instanceof Asteroid)
						&& (firstObject.checkForCollision(secondObject))) {
					resolvePlayerShipToAsteroid((PlayerShip) firstObject, (Asteroid) secondObject, spaceObjectList);
				}

				// troll scout and bullet.
				if ((firstObject instanceof TrollScout) && (secondObject instanceof Bullet)
						&& (firstObject.checkForCollision(secondObject))) {
					// make sure it is a player bullet.
					Bullet bullet = (Bullet) secondObject;
					if (Bullet.BULLET_OWNER.PLAYER == bullet.getBulletOwner()) {
						resolvePlayerBulletToTrollScout((TrollScout) firstObject, bullet, spaceObjectList);
						continue; // get a new object, since the bullet dies
					}
				}

				// troll mothership and bullet.
				if ((firstObject instanceof TrollMothership) && (secondObject instanceof Bullet)
						&& (firstObject.checkForCollision(secondObject))) {
					// make sure it is a player bullet.
					Bullet bullet = (Bullet) secondObject;
					if (Bullet.BULLET_OWNER.PLAYER == bullet.getBulletOwner()) {
						resolvePlayerBulletToTrollMothership((TrollMothership) firstObject, bullet, spaceObjectList);
						continue; // get a new object, since the bullet dies
					}
				}

				// player ship and bullet.
				if ((firstObject instanceof PlayerShip) && (secondObject instanceof Bullet)
						&& (firstObject.checkForCollision(secondObject))) {
					// make sure it is a player bullet.
					Bullet bullet = (Bullet) secondObject;
					if (Bullet.BULLET_OWNER.TROLL == bullet.getBulletOwner()) {
						resolveTrollBulletToPlayerShip((PlayerShip) firstObject, bullet, spaceObjectList);
						continue; // get a new object, since the bullet dies
					}
				}

				// player ship and bullet.
				if ((firstObject instanceof TrollPod) && (secondObject instanceof Bullet)
						&& (firstObject.checkForCollision(secondObject))) {
					// make sure it is a player bullet.
					Bullet bullet = (Bullet) secondObject;
					if (Bullet.BULLET_OWNER.PLAYER == bullet.getBulletOwner()) {
						resolveBulletToTrollPod((TrollPod) firstObject, bullet, spaceObjectList);
						continue; // get a new object, since the bullet dies
					}
				}
			}
		}
	}

	/**
	 * Helper function resolveBulletToAsteroid takes care of bullets hitting
	 * asteroids.
	 */
	private void resolveBulletToAsteroid(Asteroid asteroid, Bullet bullet,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Bullet dies.
		bullet.killObject(spaceObjectList);

		// Asteroid breaks up. Play the asteroid hit sound.
		asteroid.killObject(spaceObjectList, asterage1State.getScoreStateObject());
		soundManager.playSoundEvent(SoundEvent.ASTEROID_IMPACT);

		// award points only if it was a player bullet. Also, only player bullets may
		// spawn the alien.
		if (bullet.getBulletOwner() == Bullet.BULLET_OWNER.PLAYER) {
			switch (asteroid.getSize()) {
			case SMALL:
				asterage1State.getScoreStateObject()
						.awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_SMALL_ASTEROID);
				break;

			case MEDIUM:
				asterage1State.getScoreStateObject()
						.awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_MEDIUM_ASTEROID);
				break;

			case LARGE:
				asterage1State.getScoreStateObject()
						.awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_LARGE_ASTEROID);
				break;
			}

			checkForAlienSpawn(asteroid.getXPosition(), asteroid.getYPosition(), asterage1State, spaceObjectList);
		}
	}

	/**
	 * Helper function resolvePlayerShipToAsteroid takes care of asteroids hitting
	 * the ship.
	 */
	private void resolvePlayerShipToAsteroid(PlayerShip playerShip, Asteroid asteroid,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Player ship only collides with asteroids when living.
		if (playerShip.getShipStatus() != PlayerShip.SHIP_STATUS.ALIVE) {
			return;
		}

		// nothing happens to asteroid.

		// player ship gets damaged. Play the player ship impact sound.
		double amountDamage = Asteroid.asteroidDamage;
		playerShip.damageShields(amountDamage, spaceObjectList);
		if (false == playerShip.hasShieldsAttached()) {
			ShieldRing shieldRing = new ShieldRing(playerShip);
			playerShip.attachShields(shieldRing);
			spaceObjectList.add(shieldRing);
		} else {
			playerShip.renewShieldEffect();
		}
		// if there are shield points left, play the shield damaged sound. If not, play
		// the ship destroyed sound.
		if (PlayerShip.SHIP_STATUS.ALIVE == playerShip.getShipStatus()) {
			soundManager.playSoundEvent(SoundEvent.PLAYER_SHIELD_IMPACT);
		} else {
			soundManager.playSoundEvent(SoundEvent.PLAYER_SHIP_DESTROYED);
		}
	}

	/**
	 * PlayerBullets damage troll scout ships.
	 * 
	 * @param trollScout
	 * @param bullet
	 * @param spaceObjectList
	 */
	private void resolvePlayerBulletToTrollScout(TrollScout trollScout, Bullet bullet,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Bullet dies.
		bullet.killObject(spaceObjectList);

		// troll ship gets damaged. Play the player ship impact sound.
		double amountDamage = Bullet.bulletDamage;
		trollScout.damageShields(amountDamage, spaceObjectList);
		asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.HIT_TROLL_SHIP); // contact
																											// points.

		if (false == trollScout.hasShieldsAttached()) {
			ShieldRing shieldRing = new ShieldRing(trollScout);
			trollScout.attachShields(shieldRing);
			spaceObjectList.add(shieldRing);
		} else {
			trollScout.renewShieldEffect();
		}
		// if there are shield points left, play the shield damaged sound. If not, play
		// the ship destroyed sound.
		if (trollScout.getCurrentShields() > 0) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIELD_IMPACT);
		} else {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIP_DESTROYED);
			asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_TROLL_SHIP); // kill
																													// points.

		}
	}

	/**
	 * Player bullet damages troll mothership.
	 */
	private void resolvePlayerBulletToTrollMothership(TrollMothership mothership, Bullet bullet,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Bullet dies.
		bullet.killObject(spaceObjectList);

		// troll ship gets damaged. Play the player ship impact sound.
		double amountDamage = Bullet.bulletDamage;
		mothership.damageShields(amountDamage, spaceObjectList);
		asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.HIT_TROLL_MOTHERSHIP); // contact
																													// points.
		if (false == mothership.hasShieldsAttached()) {
			ShieldRing shieldRing = new ShieldRing(mothership);
			mothership.attachShields(shieldRing);
			spaceObjectList.add(shieldRing);
		} else {
			mothership.renewShieldEffect();
		}
		// if there are shield points left, play the shield damaged sound. If not, play
		// the ship destroyed sound.
		if (mothership.getCurrentShields() > 0.0) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIELD_IMPACT);
		} else {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIP_DESTROYED);
			asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_TROLL_MOTHERSHIP); // kill
																															// points.
		}
	}

	/**
	 * Troll bullets damage the player ship in a collision.
	 * 
	 * @param playerShip
	 * @param bullet
	 * @param spaceObjectList
	 */
	private void resolveTrollBulletToPlayerShip(PlayerShip playerShip, Bullet bullet,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Player ship only collides with bullets when living.
		if (playerShip.getShipStatus() != PlayerShip.SHIP_STATUS.ALIVE) {
			return;
		}

		// Bullet dies.
		bullet.killObject(spaceObjectList);

		// player ship gets damaged. Play the player ship impact sound.
		double amountDamage = Bullet.bulletDamage;
		playerShip.damageShields(amountDamage, spaceObjectList);
		if (false == playerShip.hasShieldsAttached()) {
			ShieldRing shieldRing = new ShieldRing(playerShip);
			playerShip.attachShields(shieldRing);
			spaceObjectList.add(shieldRing);
		} else {
			playerShip.renewShieldEffect();
		}
		// if there are shield points left, play the shield damaged sound. If not, play
		// the ship destroyed sound.
		if (PlayerShip.SHIP_STATUS.ALIVE == playerShip.getShipStatus()) {
			soundManager.playSoundEvent(SoundEvent.PLAYER_SHIELD_IMPACT);
		} else {
			soundManager.playSoundEvent(SoundEvent.PLAYER_SHIP_DESTROYED);
		}

	}

	private void resolveBulletToTrollPod(TrollPod trollPod, Bullet bullet,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Check to see if the troll pod is a player pod that has not yet reached
		// operational capacity.
		// No impact on a non-deployed player pod.
		if (trollPod instanceof PlayerPod) {
			PlayerPod playerPod = (PlayerPod) trollPod;
			if (playerPod.getPlayerPodStatus() != PlayerPod.PLAYER_POD_STATUS.OPERATIONAL) {
				return; // no effect.
			}
		}

		// else the bullet dies.
		bullet.killObject(spaceObjectList);

		// troll ship gets damaged. Play the player ship impact sound.
		double amountDamage = Bullet.bulletDamage;
		trollPod.damageShields(amountDamage, spaceObjectList);
		asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.HIT_TROLL_SHIP); // contact
																											// points.

		if (false == trollPod.hasShieldsAttached()) {
			ShieldRing shieldRing = new ShieldRing(trollPod);
			trollPod.attachShields(shieldRing);
			spaceObjectList.add(shieldRing);
		} else {
			trollPod.renewShieldEffect();
		}

		// if there are shield points left, play the shield damaged sound. If not, play
		// the ship destroyed sound.
		if (trollPod.getCurrentShields() > 0) {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIELD_IMPACT);
		} else {
			soundManager.playSoundEvent(SoundEvent.ENEMY_SHIP_DESTROYED);
			asterage1State.getScoreStateObject().awardPoints(Asterage1ScoreState.SCORE_SYSTEM.DESTROY_TROLL_SHIP); // kill
																													// points.
		}
	}

	/**
	 * See if the game board state satisfies the conditions for a level promotion.
	 * Considered a special update condition.
	 */
	private void checkForLevelPromotion(PlayerShip playerShip, ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// If the player is dead, no level promotion.
		if (PlayerShip.SHIP_STATUS.ALIVE != playerShip.getShipStatus()) {
			return;
		} // end if check for player being alive.

		for (SpaceObject currentObject : spaceObjectList) {
			// If there are any asteroids or enemy ships on the board, then the answer is
			// no.
			if ((currentObject instanceof Asteroid) || (currentObject instanceof TrollScout)
					|| (currentObject instanceof TrollMothership) || (currentObject instanceof TrollPod)) {
				return;
			} // end if check for enemies or asteroids being alive.

		} // end for loop to iterate through enemy objects.s
			// if at the end of all, we have not found a reason to deny promotion, trigger
			// it.
			// sound the promotion bell, and replace the current text message this instant.

		asterage1State.promoteToNextLevel();
		soundManager.playSoundEvent(SoundEvent.REWARD_EARNED);
		makeNewMessageText("Level " + asterage1State.getLevel() + "!", MessageText.MESSAGE_COLOR.REWARD, asterage1State,
				asterage1State.getSpaceObjectList());
	}

	/**
	 * If the player has no lives left, and the ship is destroyed, and there are no
	 * player bullets left on the screen, then it's game over for the player. Go
	 * through each check, and look for a reason to return prematurely (i.e. not
	 * declare game over.) If we make it to the end of the checklist, then it's game
	 * over for the player as well.
	 */
	private void checkForGameOver(PlayerShip playerShip, Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// If the state is already in a game over state, then no need to go through all
		// the logic again.
		if (asterage1State.getGameStatus() == Asterage1State.GAME_STATUS.GAME_OVER) {
			return; // no need to go through anything.
		} // end if check for already game over condition

		// check if there are lives left or the ship is not destroyed. If either are so,
		// then it's not game over.
		if (!((0 >= asterage1State.getExtraLives())
				&& (playerShip.getShipStatus() == PlayerShip.SHIP_STATUS.DESTROYED))) {
			return; // not over yet.
		}

		// if we make it to this, check to see if there are any player bullets or ship
		// debris left. If so, then it's not game over...yet.
		if (true == playerBulletsOrExplosionsOnGameBoard(spaceObjectList)) {
			return; // not over yet.
		}

		// once we reach here, then it's game over.
		// check to see if we have a high
		asterage1State.setGameStatus(Asterage1State.GAME_STATUS.GAME_OVER);

		long points = asterage1State.getScore();
		if (true == asterage1State.doesScoreRankTopTen(points)) {
			// play the high score music!
			soundManager.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_HIGH_SCORE);

			// get the name the player wants to enter as their high score entry.
			String highScoreName = gui.getReplyDialog("NEW HIGH SCORE!",
					"Congratulations! Your performance has earned you a place in the AsteRAGE \n"
							+ "hall of fame! Enter your name as you wish for it to appear in the \n"
							+ "high score list! (Up to 8 letters)");
			highScoreName += "         "; // pad with 8 spaces, so we can be sure to grab 8 characters.
			String highScoreNameToSave = highScoreName.substring(0, 8);
			int currentLevel = asterage1State.getLevel();

			// Save the new record.
			log.debug("{} has secured a position of fame... for now!", highScoreNameToSave);
			asterage1State.insertNewRecord(highScoreNameToSave, currentLevel, points);

		} else {
			// just play the game over music.
			soundManager.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.ASTERAGE_1_GAME_OVER);
		}
	}

	/**
	 * Helper method that checks if there are any player bullets or ship debris left
	 * on the game board.
	 * 
	 * @param spaceObjectList
	 * @return Boolean value representing if there are player bullets or ship debris
	 *         left. True = yes, false = no.
	 */
	private boolean playerBulletsOrExplosionsOnGameBoard(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// iterate through the list of space objects. If one is a player bullet, return
		// true immediately.
		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof Bullet) {
				Bullet bulletObject = (Bullet) currentObject;
				if (Bullet.BULLET_OWNER.PLAYER == bulletObject.getBulletOwner()) {
					return true; // found one.
				}
			}

			if (currentObject instanceof ShipDebrisExplosion) {
				ShipDebrisExplosion explosionObject = (ShipDebrisExplosion) currentObject;
				if (ShipDebrisExplosion.DebrisExplosionOwner.PLAYER == explosionObject.getOwner()) {
					return true; // there is ship debris left.
				}
			}
		}

		return false;
	}

	/**
	 * Trigger shield regeneration in those objects which have shields.
	 */
	private void regenerateShields(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof DeploysShields) {
				DeploysShields shieldBearingObject = (DeploysShields) currentObject;
				shieldBearingObject.regenerateShields();
			}
		}
	}

	/**
	 * Age space objects that have a limited life span. Expired objects die.
	 */
	private void ageObjects(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		for (SpaceObject currentObject : spaceObjectList) {
			if (currentObject instanceof LimitedLifespan) {
				LimitedLifespan agingObject = (LimitedLifespan) currentObject;
				// age the object, then check if it has expired.
				agingObject.ageObject();
				if (true == agingObject.checkEndOfLifespan()) {
					agingObject.killObject(spaceObjectList);

					// lastly, check if the aged object was an alien. If so, then we spawn a
					// mothership.
					if (agingObject instanceof Alien) {
						spawnTrollMothership(spaceObjectList);
					}
				}
			}
		}
	}

	/**
	 * Logic that governs whether the player can use an extra life to reappear on
	 * the board.
	 */
	private void requestRespawnPlayerShip() {
		if ((playerHasLivesRemaining()) && (noExplosionsExist())) {
			// do it, and set the game state to in progress, which it must be by default.
			asterage1State.spawnNewPlayerShip();
			asterage1State.setGameStatus(Asterage1State.GAME_STATUS.IN_PROGRESS);
			soundManager.playSoundEvent(SoundEvent.PLAYER_SHIP_APPEARS);
		} // end if clause to handle successful player respawn.
	} // end function requestRespawnPlayerShip

	/**
	 * See how many player lives are left.
	 * 
	 * @return
	 */
	private boolean playerHasLivesRemaining() {
		int livesLeft = asterage1State.getExtraLives();
		if (livesLeft >= 1) {
			return true;
		}

		return false;
	}

	/**
	 * This helper method goes through all objects currently in existence. If one is
	 * an explosion, it returns false. Else this function returns true.
	 * 
	 * @return
	 */
	private boolean noExplosionsExist() {
		for (SpaceObject currentObject : asterage1State.getSpaceObjectList()) {
			if (currentObject instanceof ShipDebrisExplosion) {
				return false;
			} // end if check for the type of object
		} // end for loop to iterate through objects.

		// else there are no explosions.
		return true;
	}

	/**
	 * Return true if it is time to do a special update. This is found by taking the
	 * modulus of the counter and the maximum counter value. If the modulus is 0,
	 * then the answer is yes.
	 * 
	 * @return
	 */
	private boolean timeToDoSpecialUpdate() {
		if (0 == (this.specialConditionCheckCounter % this.maxSpecialConditionCheckCounter)) {
			return true;
		}
		return false;
	}

	/**
	 * This method increments the special update counter by 1, and takes the result
	 * mod maximum value, so that we end up with a rotating counter.
	 */
	private void incrementSpecialUpdateCounter() {
		this.specialConditionCheckCounter += 1;
		this.specialConditionCheckCounter = this.specialConditionCheckCounter % this.maxSpecialConditionCheckCounter;
	} // end function incrementSpecialUpdateCounter

	/**
	 * Toggle the state from pause to un-paused, as long as the game is in progress.
	 * 
	 * @param asterage1State
	 */
	private void togglePause(Asterage1State asterage1State) {
		// If the game status is IN PROGRESS, set it to PAUSE. If it is PAUSE, set it to
		// IN PROGRESS.
		if (Asterage1State.GAME_STATUS.IN_PROGRESS == asterage1State.getGameStatus()) {
			asterage1State.setGameStatus(Asterage1State.GAME_STATUS.PAUSE);
			log.debug("Pause!");
		} else if (Asterage1State.GAME_STATUS.PAUSE == asterage1State.getGameStatus()) {
			asterage1State.setGameStatus(Asterage1State.GAME_STATUS.IN_PROGRESS);
			log.debug("Unpause!");
		} else {
			return; // we are not interested in pausing or unpauseing.
		}
		// if we make it this far, play the game pause sound effect.
	} // end function

	/**
	 * Check if it is time to add a troll scout. If so, invoke the troll scout
	 * generator method in the asterage state.
	 * 
	 * @param asterage1State
	 * @param spaceObjectList
	 */
	private void checkForTrollAppearance(Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		double chanceForScout = 1.0 + (asterage1State.getLevel() * 0.5);
		double randomRoll = Math.random() * 100;

		// check to see if the random chance came in under the threshold.
		if (randomRoll <= chanceForScout) {
			asterage1State.spawnNewTrollScout(spaceObjectList);
			soundManager.playSoundEvent(SoundEvent.ENEMY_APPEARS);
		}
	}

	private void spawnTrollMothership(ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		asterage1State.spawnNewTrollMothership(spaceObjectList);
		soundManager.playSoundEvent(SoundEvent.BIG_ENEMY_APPEARS);
	}

	/**
	 * Check the game state conditions to see if a special text message should be
	 * displayed.
	 */
	private void checkForSpecialMessage(PlayerShip playerShip, Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// dont bother with a status update if there is already a message.
		if (null != asterage1State.getOfficialMessage()) {
			return;
		}

		// If the game has not started, then display a "press fire to begin" message.
		if (Asterage1State.GAME_STATUS.NEW_GAME == asterage1State.getGameStatus()) {
			makeNewMessageText("Welcome to AsteRAGE! Press the 'fire' button to begin!", MessageText.MESSAGE_COLOR.INFO,
					asterage1State, spaceObjectList);
		}
		// else if the game is over, display a game over message.
		else if (Asterage1State.GAME_STATUS.GAME_OVER == asterage1State.getGameStatus()) {
			makeNewMessageText("Game Over! Press enter for new game, delete to return to menu!",
					MessageText.MESSAGE_COLOR.WARNING, asterage1State, spaceObjectList);
		}
		// else if we are paused, then display the pause message.
		else if (Asterage1State.GAME_STATUS.PAUSE == asterage1State.getGameStatus()) {
			makeNewMessageText("PAUSE", MessageText.MESSAGE_COLOR.INFO, asterage1State, spaceObjectList);
		}
	}

	/**
	 * Make the actual message object and add it to the game state per the normal
	 * way.
	 */
	private void makeNewMessageText(String theMessage, MessageText.MESSAGE_COLOR whatColor,
			Asterage1State asterage1State, ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		MessageText newMessage = new MessageText(theMessage, whatColor, asterage1State);
		spaceObjectList.add(newMessage);

	}

	/**
	 * Reset the game state to a new game.
	 */
	private void doGameReset() {
		// Reset the state, and change the song back to what is appropriate for the
		// in-game state.
		asterage1State.resetGame();
		soundManager.changeMusicSequence(SoundManager.SOUNDTRACK_SEQUENCE.NONE);
	}

	private void checkForExtraLifeAward(PlayerShip playerShip, Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		// Get the current score, and how many extra lives have already been awarded.
		// Calculate the amount of points needed to be awarded an extra life.
		int playerScore = asterage1State.getScore();
		int pointsNeededForAward = GameConstants.pointsForExtraLifeAward * (asterage1State.getLivesAwarded() + 1);

		// Check to see if the player's score is sufficient to merit an extra life. If
		// so, award it!
		// Then increment the number of awards given.
		if (playerScore > pointsNeededForAward) {
			asterage1State.incrementExtraLives();
			asterage1State.incrementLivesAwarded();
			soundManager.playSoundEvent(SoundEvent.REWARD_EARNED);
			makeNewMessageText("Extra Life!", MessageText.MESSAGE_COLOR.REWARD, asterage1State, spaceObjectList);
		}
	}

	/**
	 * Check to see if an alien should spawn.
	 */
	private void checkForAlienSpawn(double xSpawnLocation, double ySpawnLocation, Asterage1State asterage1State,
			ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		int currentLevel = asterage1State.getLevel();
		boolean alienHasSpawnedAlready = asterage1State.getAlienHasSpawnedFlag();
		double spawnChance = TrollMothership.chanceForSpawnPerLevel
				* (currentLevel - GameConstants.TrollMotherShipLevelDecrement); // incrementally increasing, .1% per
																				// level over 7.
		if ((false == alienHasSpawnedAlready) && (Math.random() < spawnChance)) {
			// create a new space alien and add it to the list of space objects.
			Alien spawnedAlien = new Alien(xSpawnLocation, ySpawnLocation);
			spaceObjectList.add(spawnedAlien);

			asterage1State.setAlienHasSpawnedFlag();
			soundManager.playSoundEvent(SoundEvent.ALIEN_EMERGES);

		}
	}

	/**
	 * Quit game and return to title screen.
	 */
	private void returnToTitleScreen() {
		controller.switchActiveState(CurrentState.TITLE_SCREEN);
	}

}
