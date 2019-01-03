package com.alex.nikitin.server.ai;

import com.alex.nikitin.server.Game;
import com.alex.nikitin.server.model.Move;
import com.alex.nikitin.server.model.Player;

import java.util.List;
import java.util.Random;

public class ZeroAlpha {
    private Player player;
    private Game game;
    private Random random;

    public ZeroAlpha(Game game, Player player) {
        this.player = player;
        this.game = game;
        this.random = new Random();
    }

    public List<Move> getMoves() {
        List<List<Move>> moves = game.getPossibleMoves();
        if (moves.isEmpty()) {
            System.out.println("You called me but I have no moves, " + player);
        }
        int r = random.nextInt(moves.size());
        return moves.get(r);
    }
}
