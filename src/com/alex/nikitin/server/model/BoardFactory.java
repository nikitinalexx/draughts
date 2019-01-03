package com.alex.nikitin.server.model;

import java.util.List;

import static com.alex.nikitin.server.model.ChangeableBoard.applyMoveToBoard;

public class BoardFactory {

    public static ChangeableBoard makeAMove(ChangeableBoard changeableBoard, List<Move> moves) {
        ChangeableBoard newBoard = new ChangeableBoard(changeableBoard);

        moves.forEach((Move move) -> applyMoveToBoard(newBoard, move));

        newBoard.setWhiteTurn(!newBoard.isWhiteTurn());

        return newBoard;
    }
}
