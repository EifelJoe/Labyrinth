package de.fhac.mazenet.server.userinterface.mazeFX;

import de.fhac.mazenet.server.Messages;
import de.fhac.mazenet.server.config.Settings;
import de.fhac.mazenet.server.tools.Debug;
import de.fhac.mazenet.server.userinterface.mazeFX.util.BetterOutputStream;
import de.fhac.mazenet.server.userinterface.mazeFX.util.ImageResourcesFX;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Richard Zameitat on 25.05.2016.
 */
public class C_MainUI implements Initializable {

    @FXML
    private Pane rootPane;

    @FXML
    private StackPane parent3D;

    @FXML
    private SubScene sub3D;

    @FXML
    private Pane cntrls3D;

    @FXML
    private Slider camZoomSlide;

    @FXML
    private TextArea logArea;

    @FXML
    private Label serverStatusText;

    @FXML
    private Button serverStart;

    @FXML
    private Button serverStop;

    @FXML
    private Spinner<Number> maxPlayer;

    @FXML
    private VBox playerStatsContrainer;

    @FXML
    private Label playerStatsPlaceholder;

    @FXML
    private BorderPane winnerPanel;

    @FXML
    private Label winnerText;

    @FXML
    private Label winnerPlayer;
    private List<Runnable> camRotateRightStartListeners = new LinkedList<>();
    private List<Runnable> camRotateRightStopListeners = new LinkedList<>();
    private List<Runnable> camRotateLeftStartListeners = new LinkedList<>();
    private List<Runnable> camRotateLeftStopListeners = new LinkedList<>();
    private List<Runnable> camRotateUpStartListeners = new LinkedList<>();
    private List<Runnable> camRotateUpStopListeners = new LinkedList<>();
    private List<Runnable> camRotateDownStartListeners = new LinkedList<>();
    private List<Runnable> camRotateDownStopListeners = new LinkedList<>();
    private List<Runnable> startServerListeners = new LinkedList<>();
    private List<Runnable> stopServerListeners = new LinkedList<>();

    public int getMaxPlayer() {
        return maxPlayer.getValue().intValue();
    }

    @FXML
    private void camRotRightBtMousePress(MouseEvent evt) {
        camRotateRightStartListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotRightBtMouseRelease(MouseEvent evt) {
        camRotateRightStopListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotLeftBtMousePress(MouseEvent evt) {
        camRotateLeftStartListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotLeftBtMouseRelease(MouseEvent evt) {
        camRotateLeftStopListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotUpBtMousePress(MouseEvent evt) {
        camRotateUpStartListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotUpBtMouseRelease(MouseEvent evt) {
        camRotateUpStopListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotDownBtMousePress(MouseEvent evt) {
        camRotateDownStartListeners.forEach(r -> r.run());
    }

    @FXML
    private void camRotDownBtMouseRelease(MouseEvent evt) {
        camRotateDownStopListeners.forEach(r -> r.run());
    }

    @FXML
    private void serverStartAction(ActionEvent aevt) {
        //TODO
        startServerListeners.forEach(r -> r.run());
    }

    @FXML
    private void serverStopAction(ActionEvent aevt) {
        //TODO
        stopServerListeners.forEach(r -> r.run());
    }

    public Pane getParent3D() {
        return parent3D;
    }

    public SubScene getSub3D() {
        return sub3D;
    }

    public Slider getCamZoomSlide() {
        return camZoomSlide;
    }

    public void addCamRotateRightStartListener(Runnable r) {
        camRotateRightStartListeners.add(r);
    }

    public void addCamRotateRightStopListener(Runnable r) {
        camRotateRightStopListeners.add(r);
    }

    public void addCamRotateLeftStartListener(Runnable r) {
        camRotateLeftStartListeners.add(r);
    }

    public void addCamRotateLeftStopListener(Runnable r) {
        camRotateLeftStopListeners.add(r);
    }

    public void addCamRotateUpStartListener(Runnable r) {
        camRotateUpStartListeners.add(r);
    }

    public void addCamRotateUpStopListener(Runnable r) {
        camRotateUpStopListeners.add(r);
    }

    public void addCamRotateDownStartListener(Runnable r) {
        camRotateDownStartListeners.add(r);
    }

    public void addCamRotateDownStopListener(Runnable r) {
        camRotateDownStopListeners.add(r);
    }

    public void addStartServerListener(Runnable r) {
        startServerListeners.add(r);
    }

    public void addStopServerListener(Runnable r) {
        stopServerListeners.add(r);
    }

    public void gameStarted() {
        serverStart.disableProperty().setValue(true);
        maxPlayer.disableProperty().setValue(true);
        serverStop.disableProperty().setValue(false);
        clearPlayerStats();
        ImageResourcesFX.reset();
        serverStatusText.setText(Messages.getString("MazeFX.status.started"));
        playerStatsPlaceholder.setVisible(true);

    }

    public void gameStopped() {
        serverStop.disableProperty().setValue(true);
        maxPlayer.disableProperty().setValue(false);
        serverStart.disableProperty().setValue(false);
        serverStatusText.setText(Messages.getString("MazeFX.status.stopped"));
    }

    public void addPlayerStat(Node statNode) {
        playerStatsPlaceholder.setVisible(false);
        playerStatsContrainer.getChildren().addAll(statNode);
        playerStatsContrainer.setPrefHeight(playerStatsContrainer.getChildren().size() * 60);
    }

    public void clearPlayerStats() {
        hideWinner();
        playerStatsContrainer.getChildren().clear();
        playerStatsContrainer.setPrefHeight(playerStatsContrainer.getChildren().size() * 60);

    }

    public void showWinner(String name) {
        winnerPlayer.setText(name);
        winnerPanel.setVisible(true);
    }

    public void hideWinner() {
        winnerPanel.setVisible(false);
    }

    private void initalizeWinnerPanelTextEffect() {
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);

        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.web("#00c300"));
        ds1.setRadius(20);
        ds1.setSpread(0.2);

        Blend blend2 = new Blend();
        blend2.setMode(BlendMode.MULTIPLY);

        InnerShadow is = new InnerShadow();
        is.setColor(Color.web("#feeb42"));
        is.setRadius(9);
        is.setChoke(0.8);
        blend2.setBottomInput(is);

        InnerShadow is1 = new InnerShadow();
        is1.setColor(Color.web("#f13a00"));
        is1.setRadius(5);
        is1.setChoke(0.4);
        blend2.setTopInput(is1);

        Blend blend1 = new Blend();
        blend1.setMode(BlendMode.MULTIPLY);
        blend1.setBottomInput(ds1);
        blend1.setTopInput(blend2);

        blend.setTopInput(blend1);

        ChangeListener<? super java.lang.Number> updateFont = (observable, oldValue, newValue) -> {
            double w = parent3D.getWidth();
            double h = parent3D.getHeight();
            double fontSize = Math.min(w, h) * 1.25;
            winnerText.setStyle("-fx-font-size: " + +fontSize + "%");
            winnerPlayer.setStyle("-fx-font-size: " + fontSize + "%");
        };
        parent3D.widthProperty().addListener(updateFont);
        parent3D.heightProperty().addListener(updateFont);
        updateFont.changed(null, null, null);

        winnerText.setEffect(blend);
        winnerPlayer.setEffect(blend);
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        Platform.runLater(() -> {
            Debug.addDebugger(new BetterOutputStream(s -> Platform.runLater(() -> logArea.appendText(s))), Settings.DEBUGLEVEL);
            initalizeWinnerPanelTextEffect();
            //HOTFIX different implementations of Spinner from OpenJFX and Oracle JavaFX
            Number v = maxPlayer.getValueFactory().getValue();
            if (v instanceof Double) {
                //OpenJFX
                maxPlayer.getValueFactory().setValue(new Double(Settings.NUMBER_OF_PLAYERS));
            } else {
                // OracleJFX
                maxPlayer.getValueFactory().setValue(Settings.NUMBER_OF_PLAYERS);
            }
        });
    }
}

