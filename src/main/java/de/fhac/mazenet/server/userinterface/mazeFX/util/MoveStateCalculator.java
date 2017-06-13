package de.fhac.mazenet.server.userinterface.mazeFX.util;

import de.fhac.mazenet.server.generated.CardType;
import de.fhac.mazenet.server.generated.MoveMessageType;
import de.fhac.mazenet.server.generated.PositionType;
import de.fhac.mazenet.server.Board;
import de.fhac.mazenet.server.Position;
import de.fhac.mazenet.server.userinterface.mazeFX.data.VectorInt2;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates different states (before shift, after shift, after pin move),
 * more precisely, (hopefully) relevant data for each of this stages.
 *
 * WARNING: Does *not* check moves for correctness!
 *
 * Unashamedly copied in parts from Game.java and MazeFX.java.
 */
public class MoveStateCalculator {

    // CONSTANTS
    /** Width of the board */
    public static final int BOARD_WIDTH = 7;
    /** Height of the board */
    public static final int BOARD_HEIGHT = 7;

    // ATTRIBUTES
    /** Move message send by a client */
    private MoveMessageType moveMessage;
    /** Current board instance */
    private Board board;

    /**
     * Creates a new Instance which will be able to calculate data for one player's turn.
     *
     * @param moveMessage Move message send by a client.
     * @param board Current instance of the game board.
     */
    public MoveStateCalculator(MoveMessageType moveMessage, Board board){
        this.moveMessage = moveMessage;
        this.board = board;
    }

    /** Cache for {@link #getShiftDelta()} */
    private VectorInt2 shiftDelta;

    /**
     * Calculates a vector which represents the translation done when shifting cards.
     *
     * @return Vector which can be added to existing cards positions for shifting.
     */
    public VectorInt2 getShiftDelta(){
        if(shiftDelta == null){
            shiftDelta = getCardShiftBy(moveMessage.getShiftPosition());
        }
        return shiftDelta;
    }

    /** Cache for {@link #getShiftCardStart()} */
    private VectorInt2 shiftCardStart;

    /**
     * Calculates the shift card's start position
     * (the position from which the shift is initiated, outside of the board)
     *
     * @return Vector for the shift card position
     */
    public VectorInt2 getShiftCardStart(){
        if(shiftCardStart==null) {
            VectorInt2 shiftTo = VectorInt2.copy(moveMessage.getShiftPosition());
            shiftCardStart = shiftTo.translate(getShiftDelta().invert());
        }
        return shiftCardStart;
    }

    /** Cache for {@link #getCardsToShift()} */
    private List<VectorInt2> cardsToShift;

    /**
     * Calculates a list of all card coordinates (currently on the board) which will get shifted
     *
     * @return List of vectors containing shifted card coordinates
     */
    public List<VectorInt2> getCardsToShift(){
        if(cardsToShift == null) {
            VectorInt2 v = getShiftCardStart();
            VectorInt2 d = getShiftDelta();
            List<VectorInt2> list = new ArrayList<>(Math.max(BOARD_WIDTH, BOARD_HEIGHT));
            while (isOnBoard(v = v.translate(d))) {
                list.add(v);
            }
            cardsToShift = list;
        }
        return cardsToShift;
    }

    /** Cache for {@link #getPushedOutPlayersPosition()} */
    private VectorInt2 pushedOutPlayersPosition;

    /**
     * Calculates the position (card coordinates) at which players will get pushed out of the board
     *
     * @return Card position as vector
     */
    public VectorInt2 getPushedOutPlayersPosition(){
        if(pushedOutPlayersPosition == null){
            Position oppositePosition = new Position(moveMessage.getShiftPosition()).getOpposite();
            pushedOutPlayersPosition = VectorInt2.copy(oppositePosition);
        }
        return pushedOutPlayersPosition;
    }

    /** Cache for {@link #getPushedOutPlayers()} */
    private List<Integer> pushedOutPlayers;

    /**
     * Calculates the IDs of all players that will be pushed out when shifting
     *
     * WARNING: do *not* use for processing in UIs, the board will already have changed!
     *
     * @return List of player IDs
     */
    public List<Integer> getPushedOutPlayers(){
        if(pushedOutPlayers == null){
            VectorInt2 position = getPushedOutPlayersPosition();
            CardType card = board.getCard(position.x, position.y);
            List<Integer> playersOnCard = card.getPin().getPlayerID();
            pushedOutPlayers = new ArrayList<>(playersOnCard);
        }
        return pushedOutPlayers;
    }

    /** Cache for {@link #getNewPlayerPosition()} */
    private VectorInt2 newPlayerPosition;

    /**
     * Calculates the new position for shifted out players
     *
     * This will be the opposite of the original position of the shifted out
     * card, or simpler: the shift card's new position.
     *
     * @return Card position as vector
     */
    public VectorInt2 getNewPlayerPosition(){
        if(newPlayerPosition == null){
            newPlayerPosition = getShiftCardStart().translate(getShiftDelta());
        }
        return newPlayerPosition;
    }

    /**
     * Calculates the position a pin has after shifting and before moving, based on the position it has before shifting.
     *
     * @param preShiftPos Player's position before using the shift card
     * @return Player's position after shifting
     */
    public VectorInt2 getPlayerPositionAfterShift(VectorInt2 preShiftPos){
        if(!getCardsToShift().contains(preShiftPos)){
            return preShiftPos;
        }
        if(preShiftPos.equals(getPushedOutPlayersPosition())){
            return getNewPlayerPosition();
        }
        return preShiftPos.translate(getShiftDelta());
    }

    /* *************************************************************
    ********                HELPER FUNCTIONS                ********
    ****************************************************************/

    /**
     * Calculates a translation vector for shifting, based on the passed shift card position
     *
     * WARNING: This function does *not* check the position's validity
     *          and may return unusable results for invalid values.
     *
     * @param position Position of the shift card
     *
     * @return Translation Vector for shifted cards
     */
    private static VectorInt2 getCardShiftBy(PositionType position) {
        if (position.getCol() == 0)
            return new VectorInt2(1, 0);
        if (position.getCol() == BOARD_WIDTH - 1)
            return new VectorInt2(-1, 0);

        if (position.getRow() == 0)
            return new VectorInt2(0, 1);
        if (position.getRow() == BOARD_HEIGHT - 1)
            return new VectorInt2(0, -1);

        return new VectorInt2(0, 0); // no idea!
    }

    /**
     * Checks if a position is on the board
     *
     * @see #isOnBoard(int, int)
     *
     * @param v Position as vector
     * @return true, if it is on the board, false otherwise
     */
    public static boolean isOnBoard(VectorInt2 v){
        return isOnBoard(v.x, v.y);
    }

    /**
     * Checks if a position is on the board
     *
     * @param x Position's X coordinate
     * @param y Position's Y coordinate
     * @return true, if it is on the board, false otherwise
     */
    public static boolean isOnBoard(int x, int y){
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT;
    }

}
