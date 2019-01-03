package com.alex.nikitin.server;

import com.alex.nikitin.server.ai.ZeroAlpha;
import com.alex.nikitin.server.model.Board;
import com.alex.nikitin.server.model.Constants;
import com.alex.nikitin.server.model.Move;
import com.alex.nikitin.server.model.Player;

import java.util.ArrayList;
import java.util.List;

import static com.alex.nikitin.server.model.Constants.MOVES_BEFORE_DRAW;

public class Game {
    private List<Board> positions = new ArrayList<>();
    private Board board;

    public Game() {
        board = new Board();
        positions.add(board);
    }

    public Board getCurrentBoard() {
        return board;
    }

    public Board getBoardOfPlayer(Player player) {
        switch(player) {
            case BLACK: return board.getReversedBoard();
            case WHITE: return getCurrentBoard();
        }
        return null;
    }

    public void performMove(List<Move> moves) {
        Board newBoard = board.performMove(moves);
        positions.add(newBoard);
        board = newBoard;
    }

    public Player whoWon() {
        if (!board.anyLeft(Constants.WHITE_CHECKERS)) {
            return Player.BLACK;
        }

        if (!board.anyLeft(Constants.BLACK_CHECKERS)) {
            return Player.WHITE;
        }

        if (getCurrentBoard().isWhiteTurn() && getBoardOfPlayer(Player.WHITE).getPossibleMoves().isEmpty()) {
            return Player.BLACK;
        }

        if (!getCurrentBoard().isWhiteTurn() && getBoardOfPlayer(Player.BLACK).getPossibleMoves().isEmpty()) {
            return Player.WHITE;
        }

        if (positions.size() <= MOVES_BEFORE_DRAW) {
            return null;
        }
        for (int i = 0; i < MOVES_BEFORE_DRAW; i++) {
            Board previous = positions.get(positions.size() - 2 - i);
            if (previous.valueChanged(board)) {
                return null;
            }
        }

        return Player.DRAW;
    }
}
