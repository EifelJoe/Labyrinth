package de.fhaachen.mazenet.server.userInterface.mazeFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import de.fhaachen.mazenet.server.Player;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by richard on 08.06.16.
 */
public class PlayerStatFX {

    public final int playerId;
    public final C_PlayerStat controller;
    public final Node root;

    public PlayerStatFX(int playerId) throws IOException {
        this.playerId = playerId;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("PlayerStat.fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("server.userInterface.messages"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
    }

    public void update(Player p){
    	
    }

}
