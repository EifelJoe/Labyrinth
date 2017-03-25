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
 * Created by Richard Zameitat on 02.03.2017.
 */
public class AnimationFactory {
	private AnimationFactory(){}

	public static double PLAYER_MOVE_HEIGHT_DELTA = 1.5;

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
			tmpT.setToX(moveTo.x);
			tmpT.setToZ(moveTo.z);
			return tmpT;
		}).collect(Collectors.toList()));
		moveDown.getChildren().addAll(players.stream().map(p->{
			TranslateTransition tmpT = new TranslateTransition(durDown, p);
			tmpT.setByY(PLAYER_MOVE_HEIGHT_DELTA);
			return tmpT;
		}).collect(Collectors.toList()));

		ExecuteTransition updateBinding = new ExecuteTransition(()->{
			players.forEach(p->{
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
	 * @return			Timeline animation for the whole move
	 */
	public static Timeline createMoveTimeline(Board b, Position from, Position to, PlayerFX player, Duration moveDelay){
		List<Position> positions;
		try {
			positions = Algorithmics.findPath(b,from,to);
			System.out.printf("PATH: %s%n",Algorithmics.pathToString(positions));
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
		System.out.println(frames);
		return new Timeline(frames.toArray(new KeyFrame[0]));
	}
}
