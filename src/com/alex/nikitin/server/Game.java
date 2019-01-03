package com.alex.nikitin.server;

import com.alex.nikitin.server.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.alex.nikitin.server.model.Constants.MOVES_BEFORE_DRAW;

public class Game {
    private List<ChangeableBoard> positions = new ArrayList<>();
    private ChangeableBoard currentBoard;

    public Game() {
        currentBoard = new ChangeableBoard();
        positions.add(currentBoard);
    }

    public ChangeableBoard getCurrentBoard() {
        return currentBoard;
    }

    public List<List<Move>> getPossibleMoves() {
        return getCurrentBoard().getPossibleMoves();
    }

    public void performMove(List<Move> moves) {
        ChangeableBoard newBoard = currentBoard.performMove(moves);
        positions.add(newBoard);
        currentBoard = newBoard;
    }

    public static Player whoWon(ChangeableBoard changeableBoard) {
        if (!BoardHelper.anyLeft(changeableBoard, Constants.WHITE_CHECKERS)) {
            return Player.BLACK;
        }

        if (!BoardHelper.anyLeft(changeableBoard, Constants.BLACK_CHECKERS)) {
            return Player.WHITE;
        }

        if (changeableBoard.isWhiteTurn() && changeableBoard.getPossibleMoves().isEmpty()) {
            return Player.BLACK;
        }

        if (!changeableBoard.isWhiteTurn() && changeableBoard.getReversedBoard().getPossibleMoves().isEmpty()) {
            return Player.WHITE;
        }

        if (changeableBoard.getNumberInSequence() <= MOVES_BEFORE_DRAW) {
            return null;
        }

        ChangeableBoard current = changeableBoard;
        for (int i = 0; i < MOVES_BEFORE_DRAW; i++) {
            ChangeableBoard previous = current.getParent();
            if (BoardHelper.valueChanged(current, previous)) {
                return null;
            }
            current = previous;
        }

        return Player.DRAW;
    }

    public Player whoWon() {
        return whoWon(currentBoard);
    }

}
