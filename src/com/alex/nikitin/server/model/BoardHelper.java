package com.alex.nikitin.server.model;

import java.util.List;
import java.util.stream.Collectors;

import static com.alex.nikitin.server.model.Checker.*;
import static com.alex.nikitin.server.model.Constants.BOARD_SIZE;

public class BoardHelper {

    public static boolean anyLeft(ChangeableBoard board, List<Checker> validCheckers) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (validCheckers.contains(board.getPosition()[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Move> reversedMoves(List<Move> moves) {
        return moves.stream().map((Move move) ->
                new Move(
                        BOARD_SIZE - move.getStartX() - 1, BOARD_SIZE - move.getStartY() - 1,
                        BOARD_SIZE - move.getEndX() - 1, BOARD_SIZE - move.getEndY() - 1)
        ).collect(Collectors.toList());
    }

    public static boolean valueChanged(ChangeableBoard firstBoard, ChangeableBoard secondBoard) {
        int firstWhiteQueenCount = 0;
        int firstBlackQueenCount = 0;
        int secondWhiteQueenCount = 0;
        int secondBlackQueenCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Checker first = firstBoard.getPosition()[i][j];
                Checker second = secondBoard.getPosition()[i][j];
                if (first == BLACK && second != BLACK) {
                    return true;
                }
                if (first == WHITE && second != WHITE) {
                    return true;
                }
                if (first == WHITE_QUEEN) {
                    firstWhiteQueenCount++;
                }
                if (first == BLACK_QUEEN) {
                    firstBlackQueenCount++;
                }
                if (second == WHITE_QUEEN) {
                    secondWhiteQueenCount++;
                }
                if (second == BLACK_QUEEN) {
                    secondBlackQueenCount++;
                }
            }
        }
        return (firstWhiteQueenCount != secondWhiteQueenCount) || (firstBlackQueenCount != secondBlackQueenCount);
    }

}
