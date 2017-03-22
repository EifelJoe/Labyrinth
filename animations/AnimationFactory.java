package de.fhaachen.mazenet.server.userInterface.mazeFX.animations;

import de.fhaachen.mazenet.server.Position;
import de.fhaachen.mazenet.server.userInterface.mazeFX.data.Translate3D;
import de.fhaachen.mazenet.server.userInterface.mazeFX.objects.CardFX;
import de.fhaachen.mazenet.server.userInterface.mazeFX.objects.PlayerFX;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Richard Zameitat on 02.03.2017.
 */
public class AnimationFactory {
	private AnimationFactory(){}

	public static Duration DEFAULT_DURATION_MOVE_PLAYERS_TO_CARD = Duration.millis(300);

	public static double PLAYER_MOVE_HEIGHT_DELTA = 1.5;

	public static Animation moveShiftedOutPlayers(List<PlayerFX> players, Translate3D moveTo, CardFX bindTo) {
		return moveShiftedOutPlayers(players, moveTo, bindTo, DEFAULT_DURATION_MOVE_PLAYERS_TO_CARD);
	}
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
}
