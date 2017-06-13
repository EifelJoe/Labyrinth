package de.fhac.mazenet.server.userinterface.mazeFX.animations;

import de.fhac.mazenet.server.*;
import de.fhac.mazenet.server.userinterface.mazeFX.MazeFX;
import de.fhac.mazenet.server.userinterface.mazeFX.data.*;
import de.fhac.mazenet.server.userinterface.mazeFX.objects.*;
import de.fhac.mazenet.server.userinterface.mazeFX.util.Algorithmics;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collection of method with the purpose of creating animation for the {@link MazeFX MazeFX user interface}.
 *
 * @author Richard Zameitat
 */
public class AnimationFactory {
	/**
	 * Private constructor to prevent instantiation of this class
	 */
	private AnimationFactory(){}

	/**
	 * Amount of height to which shifted-out players get lifted up when moving them back to the board.
	 *
	 * @see #moveShiftedOutPlayers(List, Translate3D, CardFX, Duration)
	 */
	public static double PLAYER_MOVE_HEIGHT_DELTA = 1.5;

	/**
	 * Constructs the animation for moving shifted-out players from the shifter out card to their new position.
	 *
	 * For this, all given players get lifted up by {@link #PLAYER_MOVE_HEIGHT_DELTA}, moved to the target position,
	 * lowered down to their original height and filly get bound to the new card.
	 *
	 * @param players	Players to move
	 * @param moveTo	Target position
	 * @param bindTo	Target card, to which the pins should be bound after the animation
	 * @param duration	Duration of the whole animation
	 * @return	Complete animation for moving the players (EmptyTransition if no players are given)
	 */
	public static Animation moveShiftedOutPlayers(List<PlayerFX> players, Translate3D moveTo, CardFX bindTo, Duration duration){
		if(players.isEmpty()){
			return new EmptyTransition();
		}
		final Duration
				durUp = duration.divide(4),
				durXZ = duration.divide(2),
				durDown = duration.divide(4);
		ParallelTransition
				moveUp = new ParallelTransition(),
				moveXZ = new ParallelTransition(),
				moveDown = new ParallelTransition();

		moveUp.getChildren().addAll(players.stream().map(p->{
			TranslateTransition tmpT = new TranslateTransition(durUp, p);
			tmpT.setByY(-PLAYER_MOVE_HEIGHT_DELTA);
			return tmpT;
		}).collect(Collectors.toList()));
		moveXZ.getChildren().addAll(players.stream().map(p->{
			TranslateTransition tmpT = new TranslateTransition(durXZ, p);
			Translate3D moveToTmp = moveTo.translate(p.getOffset());
			tmpT.setToX(moveToTmp.x);
			tmpT.setToZ(moveToTmp.z);
			return tmpT;
		}).collect(Collectors.toList()));
		moveDown.getChildren().addAll(players.stream().map(p->{
			TranslateTransition tmpT = new TranslateTransition(durDown, p);
			tmpT.setByY(PLAYER_MOVE_HEIGHT_DELTA);
			return tmpT;
		}).collect(Collectors.toList()));

		ExecuteTransition updateBinding = new ExecuteTransition(()->{
			players.forEach(p->{
				if(p.getBoundCard()==null){
					// This is the active player which might make a move after this transition
					// and binding the player now can cause bugs, because the pin binding is
					// already managed before shifting and after moving.
					return;
				}
				p.bindToCard(bindTo);
			});
		});

		return new SequentialTransition(moveUp,moveXZ,moveDown,updateBinding);
	}

	/**
	 * Constructs the player movement animation
	 *
	 * Tries to find an actual possible path. If that fails, a straight line is used.
	 *
	 * @param b			Board instance to use (for path calculations)
	 * @param from		Start position
	 * @param to		Destination position
	 * @param player	Payer which shall be moved (used for graphical aspects, e.g. offsets)
	 * @param moveDelay	Duration of each animation step
	 * @return	Timeline animation for the whole move
	 */
	public static Timeline createMoveTimeline(Board b, Position from, Position to, PlayerFX player, Duration moveDelay){
		List<Position> positions;
		try {
			positions = Algorithmics.findPath(b,from,to);
		}catch(Exception e){
			e.printStackTrace();
			positions = new LinkedList<>();
		}

		Wrapper<Integer> frameNo = new Wrapper<>(0);
		List<KeyFrame> frames = positions.stream().sequential().map(p->{
			Translate3D newPinOffset = player.getOffset();
			Translate3D newPinTr = MazeFX.getCardTranslateForPosition(p.getCol(), p.getRow())
					.translate(newPinOffset);

			return new KeyFrame(moveDelay.multiply(++frameNo.val),
					new KeyValue(player.translateXProperty(),newPinTr.x),
					new KeyValue(player.translateYProperty(),newPinTr.y),
					new KeyValue(player.translateZProperty(),newPinTr.z)
			);
		}).collect(Collectors.toList());
		return new Timeline(frames.toArray(new KeyFrame[0]));
	}
}
