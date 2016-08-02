package de.fhaachen.mazenet.server.userInterface.mazeFX;/**
														* Created by Richard Zameitat on 25.05.2016.
														*/

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.fhaachen.mazenet.config.Settings;
import de.fhaachen.mazenet.generated.CardType;
import de.fhaachen.mazenet.generated.MoveMessageType;
import de.fhaachen.mazenet.generated.PositionType;
import de.fhaachen.mazenet.server.*;
import de.fhaachen.mazenet.server.userInterface.Messages;
import de.fhaachen.mazenet.server.userInterface.UI;
import de.fhaachen.mazenet.server.userInterface.mazeFX.objects.CardFX;
import de.fhaachen.mazenet.server.userInterface.mazeFX.objects.PlayerFX;
import de.fhaachen.mazenet.server.userInterface.mazeFX.animations.AddTransition;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.Algorithmics;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.FakeTranslateBinding;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.Translate3D;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.Wrapper;
import de.fhaachen.mazenet.tools.Debug;
import de.fhaachen.mazenet.tools.DebugLevel;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MazeFX extends Application implements UI {

	// DIRTY HACK FOR GETTING AN INSTANCE STARTS HERE
	// (JavaFX ist not very good at creating instances ...)
	private static CountDownLatch instanceCreated = new CountDownLatch(1);
	private static MazeFX lastInstance = null;

	public MazeFX() {

	}

	public synchronized static MazeFX newInstance() {
		new Thread(() -> Application.launch(MazeFX.class)).start();
		try {
			instanceCreated.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		MazeFX instance = lastInstance;
		lastInstance = null;
		instanceCreated = new CountDownLatch(1);
		return instance;
	}

	private void instanceReady() {
		lastInstance = this;
		instanceCreated.countDown();
	}
	// END OF HACK

	private static final int BOARD_WIDTH = 7;
	private static final int BOARD_HEIGHT = 7;

	// private static final Translate3D SHIFT_CARD_TRANSLATE = new
	// Translate3D(0,-0.2,-(BOARD_HEIGHT/2.+1));
	private static final Translate3D SHIFT_CARD_TRANSLATE = new Translate3D(0, -3.3, 0);

	private Game game;
	private Board board;

	private Stage primaryStage;
	private Parent root;
	private C_MainUI controller;
	private Group scene3dRoot;

	private CardFX[][] boardCards;
	private CardFX shiftCard;
	private Map<Integer, PlayerFX> players;
	private Map<Integer, PlayerStatFX> playerStats = new HashMap<>();
	private PlayerFX currentPlayer;

	private static final double CAM_ROTATE_X_INITIAL = -40;
	private static final double CAM_ROTATE_Y_INITIAL = 15;
	private Rotate camRotateX = new Rotate(CAM_ROTATE_X_INITIAL, Rotate.X_AXIS);
	private Rotate camRotateY = new Rotate(CAM_ROTATE_Y_INITIAL, Rotate.Y_AXIS);

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		primaryStage.onCloseRequestProperty().setValue(e -> {
			System.exit(0);
		});
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("MainUI.fxml")); //$NON-NLS-1$
		fxmlLoader.setResources(ResourceBundle.getBundle("de.fhaachen.mazenet.server.userInterface.messages")); //$NON-NLS-1$
		root = fxmlLoader.load();
		controller = fxmlLoader.getController();

		init3dStuff();

		controller.addStartServerListener(this::startActionPerformed);
		controller.addStopServerListener(this::stopActionPerformed);

		primaryStage.setTitle(Messages.getString("MazeFX.WindowTitle")); //$NON-NLS-1$
		Scene scene = new Scene(root, 1000, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		instanceReady();
	}

	private PlayerStatFX createPlayerStat(int teamId) throws IOException {
		return new PlayerStatFX(teamId, board);
	}

	private void updatePlayerStats(List<Player> stats, Integer current) {
		currentPlayer = players.get(current);
		stats.forEach(p -> {
			try {
				PlayerStatFX stat = playerStats.get(p.getID());
				if (stat == null) {
					playerStats.put(p.getID(), stat = createPlayerStat(p.getID()));
					controller.addPlayerStat(stat.root);
				}
				stat.update(p, board);
				stat.active(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		playerStats.get(current).active(true);

	}

	private void startActionPerformed() {
		controller.gameStarted();
		Debug.print("MazeFX.startactionPerformed", DebugLevel.DEFAULT); //$NON-NLS-1$
		Settings.DEFAULT_PLAYERS = controller.getMaxPlayer();
		if (game == null) {
			setGame(new Game());
		}
		String[] arguments = new String[0];
		game.parsArgs(arguments);
		game.setUserinterface(this);
		controller.clearPlayerStats();
		// $NON-NLS-1$
		game.start();
	}

	private void stopActionPerformed() {
		Debug.print("MazeFX.stopactionPerformed", DebugLevel.DEFAULT); //$NON-NLS-1$
		if (game != null) {
			game.stopGame();
			game = null;
		}
		game = new Game();
		controller.gameStopped();
	}

	private void init3dStuff() {
		// scene graph
		scene3dRoot = new Group();

		Pane parent3d = controller.getParent3D();
		SubScene sub3d = controller.getSub3D();
		// replacing original Subscene with antialised one ...
		// TODO: do it in a nicer way!
		parent3d.getChildren().remove(sub3d);
		sub3d = new SubScene(scene3dRoot, 300, 300, true, SceneAntialiasing.BALANCED);
		parent3d.getChildren().add(0, sub3d);
		sub3d.heightProperty().bind(parent3d.heightProperty());
		sub3d.widthProperty().bind(parent3d.widthProperty());

		Translate camTranZ = new Translate(0, 0, -15);

		// Create and position camera
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.getTransforms().addAll(camRotateY, camRotateX, camTranZ);

		camTranZ.zProperty().bind(controller.getCamZoomSlide().valueProperty());

		// create rotation animations
		// rotate right
		AddTransition camRotR = new AddTransition(Duration.millis(3000),camRotateY.angleProperty(),360);
		camRotR.setInterpolator(Interpolator.LINEAR);
		camRotR.setCycleCount(Animation.INDEFINITE);
		camRotR.setAutoReverse(false);
		controller.addCamRotateRightStartListener(camRotR::play);
		controller.addCamRotateRightStopListener(camRotR::stop);

		// rotate left
		AddTransition camRotL = new AddTransition(Duration.millis(3000), camRotateY.angleProperty(), -360);
		camRotL.setInterpolator(Interpolator.LINEAR);
		camRotL.setCycleCount(Animation.INDEFINITE);
		camRotL.setAutoReverse(false);
		controller.addCamRotateLeftStartListener(camRotL::play);
		controller.addCamRotateLeftStopListener(camRotL::stop);

		// rotate up
		AddTransition camRotU = new AddTransition(Duration.millis(3000),camRotateX.angleProperty(),-360);
		camRotU.setLowerLimit(-90);
		camRotU.setInterpolator(Interpolator.LINEAR);
		camRotU.setCycleCount(Animation.INDEFINITE);
		camRotU.setAutoReverse(false);
		controller.addCamRotateUpStartListener(camRotU::play);
		controller.addCamRotateUpStopListener(camRotU::stop);

		// rotate down
		AddTransition camRotD = new AddTransition(Duration.millis(3000), camRotateX.angleProperty(),360);
		camRotD.setUpperLimit(90);
		camRotD.setInterpolator(Interpolator.LINEAR);
		camRotD.setCycleCount(Animation.INDEFINITE);
		camRotD.setAutoReverse(false);
		controller.addCamRotateDownStartListener(camRotD::play);
		controller.addCamRotateDownStopListener(camRotD::stop);

		// stop all animations when focus is lost
		primaryStage.focusedProperty().addListener((ov, o, n) -> {
			if (!n) {
				camRotR.stop();
				camRotL.stop();
				camRotU.stop();
				camRotD.stop();
			}
		});

		// add stuff to scene graph
		scene3dRoot.getChildren().add(camera);
		sub3d.setFill(Color.ANTIQUEWHITE);
		sub3d.setCamera(camera);

		Box c1 = new Box(1, 0.1, 1);
		c1.setMaterial(new PhongMaterial(Color.RED));
		c1.setDrawMode(DrawMode.FILL);
		// scene3dRoot.getChildren().add(c1);
	}

	private static Translate3D getCardTranslateForPosition(int x, int z) {
		final double midX = BOARD_WIDTH / 2.;
		final double midZ = BOARD_HEIGHT / 2.;
		final double offX = 0.5;
		final double offZ = -0.5;
		double newX = (x - midX) * 1 + offX;
		double newZ = (midZ - z) * 1 + offZ;
		return new Translate3D(newX, 0, newZ);
	}

	private static Translate3D getCardShiftBy(PositionType posT) {
		if (posT.getCol() == 0)
			return new Translate3D(1, 0, 0);
		if (posT.getCol() == BOARD_WIDTH - 1)
			return new Translate3D(-1, 0, 0);

		if (posT.getRow() == 0)
			return new Translate3D(0, 0, -1);
		if (posT.getRow() == BOARD_HEIGHT - 1)
			return new Translate3D(0, 0, 1);

		return new Translate3D(0, 0, 0); // no idea!
	}

	private static Translate3D getCardTranslateForShiftStart(PositionType posT) {
		return getCardTranslateForPosition(posT.getCol(), posT.getRow()).translate(getCardShiftBy(posT).invert());
	}

	private List<CardFX> updateAndGetShiftedCards(PositionType shiftPos) {
		List<CardFX> cards = new LinkedList<>();
		cards.add(shiftCard);
		CardFX oldShiftCard = shiftCard;

		if (shiftPos.getCol() == 0) {
			int sRow = shiftPos.getRow();
			cards.add(shiftCard = boardCards[sRow][BOARD_WIDTH - 1]);
			for (int x = BOARD_WIDTH - 1; x > 0; cards.add(boardCards[sRow][x] = boardCards[sRow][x - 1]), x--)
				;
			boardCards[sRow][0] = oldShiftCard;
		} else if (shiftPos.getCol() == BOARD_WIDTH - 1) {
			int sRow = shiftPos.getRow();
			cards.add(shiftCard = boardCards[sRow][0]);
			for (int x = 0; x < BOARD_WIDTH - 1; cards.add(boardCards[sRow][x] = boardCards[sRow][x + 1]), x++)
				;
			boardCards[sRow][BOARD_WIDTH - 1] = oldShiftCard;
		} else if (shiftPos.getRow() == 0) {
			int sCol = shiftPos.getCol();
			cards.add(shiftCard = boardCards[BOARD_HEIGHT - 1][sCol]);
			for (int z = BOARD_HEIGHT - 1; z > 0; cards.add(boardCards[z][sCol] = boardCards[z - 1][sCol]), z--)
				;
			boardCards[0][sCol] = oldShiftCard;
		} else if (shiftPos.getRow() == BOARD_HEIGHT - 1) {
			int sCol = shiftPos.getCol();
			cards.add(shiftCard = boardCards[0][sCol]);
			for (int z = 0; z < BOARD_HEIGHT - 1; cards.add(boardCards[z][sCol] = boardCards[z + 1][sCol]), z++)
				;
			boardCards[BOARD_HEIGHT - 1][sCol] = oldShiftCard;
		}

		return cards;
	}

	private void clearBoard() {
		currentPlayer = null;
		if (players != null) {
			playerStats = new HashMap<>();
			scene3dRoot.getChildren().removeAll(players.values());
			players = null;
		}
		if (shiftCard != null) {
			shiftCard.removeFrom(scene3dRoot);
			shiftCard = null;
		}
		if (boardCards != null) {
			for (CardFX[] ca : boardCards) {
				for (CardFX c : ca) {
					c.removeFrom(scene3dRoot);
				}
			}
		}
		this.board = null;
	}

	private void initFromBoard(Board b) {
		System.out.println(b);
		clearBoard();
		this.board = b;
		players = new HashMap<>();
		boardCards = new CardFX[BOARD_HEIGHT][BOARD_WIDTH];
		for (int z = 0; z < BOARD_HEIGHT; z++) {
			for (int x = 0; x < BOARD_WIDTH; x++) {
				CardType ct = b.getCard(z, x);
				CardFX card3d = new CardFX(ct, scene3dRoot);
				boardCards[z][x] = card3d;
				getCardTranslateForPosition(x, z).applyTo(card3d);
				scene3dRoot.getChildren().add(card3d);
				CardType.Pin pin = ct.getPin();
				if (pin != null) {
					pin.getPlayerID().forEach(pid -> {
						PlayerFX player = new PlayerFX(pid, card3d);
						players.put(pid, player);
						scene3dRoot.getChildren().add(player);
					});
				}
			}
		}
		CardType ct = b.getShiftCard();
		shiftCard = new CardFX(ct, scene3dRoot);
		SHIFT_CARD_TRANSLATE.applyTo(shiftCard);
		scene3dRoot.getChildren().add(shiftCard);
	}

	private void animateMove(MoveMessageType mm, Board b, long mvD, long shifD, boolean treasureReached, CountDownLatch lock) {
		final Duration durBefore = Duration.millis(shifD / 3);
		final Duration durShift = Duration.millis(shifD / 3);
		final Duration durAfter = Duration.millis(shifD / 3);

		final Duration durMove = Duration.millis(mvD);

		System.out.println(players);
		System.out.println(currentPlayer);
		final PlayerFX pin = currentPlayer != null ? currentPlayer : players.getOrDefault(1, null);
		FakeTranslateBinding pinBind = null;
		if (pin.getBoundCard() != null) {
			pinBind = new FakeTranslateBinding(pin, pin.getBoundCard(), pin.getOffset());
			pin.unbindFromCard();
			pinBind.bind();
		}
		FakeTranslateBinding pinBind_final = pinBind;

		CardFX shiftCardC = shiftCard;
		Card c = new Card(mm.getShiftCard());
		PositionType newCardPos = mm.getShiftPosition();
		int newRotation = c.getOrientation().value();
		// prevent rotating > 180°
		int oldRotation = shiftCardC.rotateProperty().intValue();
		int rotationDelta = newRotation-oldRotation;
		if(rotationDelta > 180){
			shiftCardC.rotateProperty().setValue(oldRotation+360);
		}else if(rotationDelta < -180){
			shiftCardC.rotateProperty().setValue(oldRotation-360);
		}

		Translate3D newCardBeforeShiftT = getCardTranslateForShiftStart(newCardPos);

		// before before
		// TODO: less time for "before before" more time for "before"
		TranslateTransition animBeforeBefore = new TranslateTransition(durAfter, shiftCardC);
		//animBeforeBefore.setToX(SHIFT_CARD_TRANSLATE.x);
		animBeforeBefore.setToY(SHIFT_CARD_TRANSLATE.y);
		//animBeforeBefore.setToZ(SHIFT_CARD_TRANSLATE.z);

		// before shift
		RotateTransition cardRotateBeforeT = new RotateTransition(durBefore, shiftCardC);
		cardRotateBeforeT.setToAngle(newRotation);
		TranslateTransition cardTranslateBeforeT = new TranslateTransition(durBefore, shiftCardC);
		cardTranslateBeforeT.setToX(newCardBeforeShiftT.x);
		cardTranslateBeforeT.setToY(newCardBeforeShiftT.y);
		cardTranslateBeforeT.setToZ(newCardBeforeShiftT.z);
		Animation animBefore = new ParallelTransition(cardRotateBeforeT, new SequentialTransition(animBeforeBefore,cardTranslateBeforeT));

		// shifting
		Translate3D shiftTranslate = getCardShiftBy(newCardPos);
		List<CardFX> shiftCards = updateAndGetShiftedCards(newCardPos);
		Animation[] shiftAnims = new Animation[shiftCards.size()];
		int i = 0;
		for (CardFX crd : shiftCards) {
			TranslateTransition tmpT = new TranslateTransition(durShift, crd);
			tmpT.setByX(shiftTranslate.x);
			tmpT.setByY(shiftTranslate.y);
			tmpT.setByZ(shiftTranslate.z);
			shiftAnims[i++] = tmpT;
		}
		Animation animShift = new ParallelTransition(shiftAnims);

		// after
		TranslateTransition animAfter = new TranslateTransition(durAfter, shiftCard);
		animAfter.setToX(SHIFT_CARD_TRANSLATE.x);
		animAfter.setToY(SHIFT_CARD_TRANSLATE.y);
		animAfter.setToZ(SHIFT_CARD_TRANSLATE.z);

		// todo: place player on board again if shifted "out"
		// ... should be possible after/while calling updateAndGetShiftedCards(...)


		PositionType newPinPos = mm.getNewPinPos();

		// TODO: update player stats to refelct positions after shifting
		// ... or ... use onFinish, calc position from translate and THEN create further animations ...
		Timeline moveAnim = createMoveTimeline(mm,b,durMove);

		/*
		 * ExecuteTransition rebindTr = new ExecuteTransition(()->{
		 * pin.bindToCard(boardCards[newPinPos.getRow()][newPinPos.getCol()]);
		 * System.out.println("bind"); });
		 * 
		 * 
		 * 
		 * ExecuteTransition debugTr = new ExecuteTransition(()->{
		 * System.out.println("\n\nSTARTING TRANSITION!"); });
		 * 
		 * PauseTransition debugTr2 = new PauseTransition(durMove);/
		 **/
		SequentialTransition allTr = new SequentialTransition(animBefore, animShift, /*animAfter,*/ moveAnim);
		allTr.setInterpolator(Interpolator.LINEAR);
		allTr.setOnFinished(e -> {
			//System.out.println("yoyo .. done!");
			if (pinBind_final != null) {
				pinBind_final.unbind();
			}
			if (treasureReached) {
				boardCards[newPinPos.getRow()][newPinPos.getCol()].getTreasure().treasureFound();
			}
			pin.bindToCard(boardCards[newPinPos.getRow()][newPinPos.getCol()]);
			lock.countDown();
		});
		allTr.play();
	}

	@Override
	public void displayMove(MoveMessageType mm, Board b, long moveDelay, long shiftDelay, boolean treasureReached) {
		CountDownLatch lock = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				animateMove(mm, b, moveDelay, shiftDelay, treasureReached, lock);
			}catch(Exception e){
				lock.countDown();
			}
		});

		// make it blocking!
		do {
			try {
				lock.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(lock.getCount()!=0);
	}

	private Timeline createMoveTimeline(MoveMessageType mm, Board b, Duration moveDelay){
		PlayerFX pin = currentPlayer;
		PositionType playerPosition = playerStats.get(pin.playerId).getPosition();
		List<Position> positions;
		try {
			// TODO: maybe improve MoveMessage to save "old" pin position
			Position from = new Position(playerPosition);
			Position to = new Position(mm.getNewPinPos());
			positions = Algorithmics.findPath(b,from,to);
			System.out.printf("PATH: %s%n",Algorithmics.pathToString(positions));
		}catch(Exception e){
			e.printStackTrace();
			positions = new LinkedList<>();
		}

		Wrapper<Integer> frameNo = new Wrapper<>(0);
		List<KeyFrame> frames = positions.stream().sequential().map(p->{
			Translate3D newPinOffset = pin.getOffset();
			Translate3D newPinTr = getCardTranslateForPosition(p.getCol(), p.getRow())
					.translate(newPinOffset);

			return new KeyFrame(moveDelay.multiply(++frameNo.val),
					new KeyValue(pin.translateXProperty(),newPinTr.x),
					new KeyValue(pin.translateYProperty(),newPinTr.y),
					new KeyValue(pin.translateZProperty(),newPinTr.z)
				);
		}).collect(Collectors.toList());
		System.out.println(frames);
		return new Timeline(frames.toArray(new KeyFrame[0]));
	}

	@Override
	public void updatePlayerStatistics(List<Player> stats, Integer current) {
		Platform.runLater(() -> this.updatePlayerStats(stats, current));
	}

	@Override
	public void init(Board b) {
		Platform.runLater(() -> initFromBoard(b));
	}

	@Override
	public void setGame(Game g) {
		this.game = g;
		if (g == null) {
			// Platform.runLater(()->clearBoard());
		} else {
			// g.start();
		}
	}

	@Override
	public void gameEnded(Player winner) {
		controller.gameStopped();
	}
}
