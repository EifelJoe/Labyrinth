package de.fhac.mazenet.server.userinterface.mazeFX;

import de.fhac.mazenet.server.userinterface.mazeFX.objects.PlayerFX;
import de.fhac.mazenet.server.userinterface.mazeFX.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Richard Zameitat on 26.05.2016.
 */
public class C_PlayerStat implements Initializable {

	private IntegerProperty treasuresFound = new SimpleIntegerProperty(-0xACE);
	private IntegerProperty treasuresRemaining = new SimpleIntegerProperty(0xACE);
	private NumberBinding treasuresTotal = Bindings.add(treasuresFound,treasuresRemaining);
	private NumberBinding treasurePercentage = Bindings.divide(treasuresFound, Bindings.multiply(1.,treasuresTotal));

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
    private Label numTotal;
    
    @FXML
    private Label activePlayer;

	@FXML
	private ProgressBar treasureProgress;

    public void setTeamId(int playerId){
        this.teamId.textProperty().setValue(Integer.toString(playerId));
        Color c = PlayerFX.playerIdToColor(playerId);
        this.teamId.setStyle("-fx-border-color: " + Converter.colorToRgbHex(c));
    }

    public void setPlayerName(String playerName){
        this.playerName.textProperty().setValue(playerName);
    }

	public void setTreasureImage(String treasureImage) {
		this.treasureImage.setImage(ImageResourcesFX.getImage(treasureImage));
	}

	public void setNumFound(int numFound) {
		treasuresFound.setValue(numFound);
	}

	public void setNumRemaining(int numRemaining) {
		treasuresRemaining.setValue(numRemaining);
	}
	
	public void setActive(boolean act) {
		if (act) {
			if (!root.getStyleClass().contains("active")) {
				root.getStyleClass().add("active");
			}
			activePlayer.setText(">");
		} else {
			root.getStyleClass().remove("active");
			activePlayer.setText("");
		}
	}

	public void setWinner(){
		root.getStyleClass().remove("active");
		activePlayer.setText("");
		root.getStyleClass().add("winner");
	}

	private void createProgressBindings(){
		numFound.setText(treasuresFound.getValue().toString());
		numTotal.setText(treasuresRemaining.getValue().toString());

		treasuresTotal.isEqualTo(0).addListener((observable, oldValue, newValue) -> {
			if(newValue){
				treasureProgress.progressProperty().setValue(ProgressBar.INDETERMINATE_PROGRESS);
			}
		});
		treasurePercentage.addListener((observable, oldValue, newValue) -> {
			treasureProgress.progressProperty().setValue(newValue);
		});
		treasuresFound.addListener((observable, oldValue, newValue) -> {
			numFound.setText(newValue.toString());
		});
		treasuresTotal.addListener((observable, oldValue, newValue) -> {
			numTotal.setText(newValue.toString());
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createProgressBindings();
	}
}
