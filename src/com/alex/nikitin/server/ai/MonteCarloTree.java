package com.alex.nikitin.server.ai;

import com.alex.nikitin.server.Game;
import com.alex.nikitin.server.model.ChangeableBoard;
import com.alex.nikitin.server.model.Move;
import com.alex.nikitin.server.model.Player;

import java.util.Collections;
import java.util.List;

public class MonteCarloTree {

    /*
    public double search(ChangeableBoard s, Game game, Nnet nnet) {
        Player player = game.whoWon(s);
        if (game.gameEnded(s))
            return -game.gameReward(s);

        if (!visited.contains(s)) {
            visited.add(s);
            P[s] = nnet.predict(s);
            return -P[s];
        }


        double max_u = Double.MIN_VALUE;
        List<Move> best_a = Collections.emptyList();

        List<List<Move>> possibleMoves = game.getCurrentBoard().getPossibleMoves();

        for (List<Move> a : possibleMoves) {
            u = Q[s][a] + c_puct*P[s][a]*sqrt(sum(N[s]))/(1+N[s][a]);
            if (u>max_u) {
                max_u = u
                best_a = a;
            }
        }
        List<Move> a = best_a;

        sp = game.nextState(s, a);
        v = search(sp, game, nnet);
        Q[s][a] = (N[s][a]*Q[s][a] + v)/(N[s][a]+1);
        N[s][a] += 1;

        return -v;
    }*/

}
