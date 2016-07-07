package de.fhaachen.mazenet.server.userInterface.mazeFX;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Created by Richard Zameitat on 26.05.2016.
 */
public class C_PlayerStat {
    @FXML
    private GridPane root;

    @FXML
    private Label teamId;

    @FXML
    private Label playerName;

    @FXML
    private ImageView treasureImage;

    @FXML
    private Label numFound;

    @FXML
    private Label numRemaining;

    public void setTeamId(String id){
        this.teamId.textProperty().setValue(id);
    }

    public void setPlayerName(String playerName){
        this.playerName.textProperty().setValue(playerName);
    }
}
