package com.alex.nikitin.server;

import com.alex.nikitin.server.model.Board;
import com.alex.nikitin.server.model.Constants;
import com.alex.nikitin.server.model.Move;
import com.alex.nikitin.server.model.Player;

import java.util.ArrayList;
import java.util.List;

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

        //check if anyone is fully blocked then another won

        //last 3 times the same position, то ничья
        //соотношение сил не поменялось за 15 ходов, и ни одна простая фишка не сделала хода, то ничья

        //TODO
        return null;//noone won yet
    }
}
