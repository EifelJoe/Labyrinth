package de.fhac.mazenet.server.userinterface.mazeFX;

import de.fhac.mazenet.server.generated.PositionType;
import de.fhac.mazenet.server.Board;
import de.fhac.mazenet.server.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by richard on 08.06.16.
 */
public class PlayerStatFX {

	public final int playerId;
	public final C_PlayerStat controller;
	public final Node root;
	private int treasureFound;
	private int cachedTreasuresRemaining;
	private PositionType position;
	private Player player;

	public PlayerStatFX(int playerId, Board board) throws IOException {
		this.playerId = playerId;

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/layouts/PlayerStat.fxml")); //$NON-NLS-1$
		fxmlLoader.setResources(ResourceBundle.getBundle("locale")); //$NON-NLS-1$
		root = fxmlLoader.load();
		controller = fxmlLoader.getController();
		// da die Nummer der zu suchenden Schätze 
		// nicht bekannt ist wird zuerst auf 0 erhöht
		treasureFound = -1;
		position = board.findPlayer(playerId);
	}

	public void update(Player p, Board board) {
		if (!(cachedTreasuresRemaining == p.treasuresToGo())) {
			controller.setNumFound(++treasureFound);
		}
		cachedTreasuresRemaining = p.treasuresToGo();
		controller.setTeamId(playerId);
		controller.setPlayerName(p.getName());
		controller.setNumRemaining(p.treasuresToGo());
		controller.setTreasureImage(p.getCurrentTreasure().value());
		position = board.findPlayer(playerId);
		this.player = p;
	}

	public void setWinner(){
		controller.setNumFound(++treasureFound);
		controller.setNumRemaining(0);
		controller.setWinner();
	}

	public void active(boolean act){
		controller.setActive(act);
	}

	public PositionType getPosition(){
		return position;
	}

	public Player getPlayer(){ return player; }

}
