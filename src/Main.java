import com.alex.nikitin.server.MyCanvas;
import com.alex.nikitin.server.Server;
import com.alex.nikitin.server.ai.ZeroAlpha;
import com.alex.nikitin.server.model.Constants;
import com.alex.nikitin.server.Game;
import com.alex.nikitin.server.model.Move;
import com.alex.nikitin.server.model.Player;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.alex.nikitin.server.model.Constants.*;
import static java.lang.Integer.parseInt;

public class Main {
    private static final boolean SINGLE_PLAYER = false;
    private static final int SLEEP_TIME = 0;
    private static final int NUMBER_OF_GAMES = 100000;

    public static void main(String[] args) throws InterruptedException {


        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, BOARD_WIDTH + X_OFFSET, BOARD_WIDTH + Y_OFFSET);
        window.setVisible(true);

        if (SINGLE_PLAYER) {
            playSinglePlayerGame(Server.createNewGame(), window);
        } else {
            int whiteWon = 0;
            int blackWon = 0;
            int draw = 0;

            for (int i = 0; i < NUMBER_OF_GAMES; i++) {
                window.getContentPane().removeAll();
                Game game = Server.createNewGame();
                window.getContentPane().add(new MyCanvas(game));
                window.setVisible(true);

                ZeroAlpha zeroAlpha = new ZeroAlpha(game, Player.WHITE);
                Player winner;
                while ((winner = game.whoWon()) == null) {
                    Thread.sleep(SLEEP_TIME);
                    game.performMove(zeroAlpha.getMoves());
                    window.repaint();
                }
                if (winner == Player.BLACK) {
                    blackWon++;
                } else if (winner == Player.WHITE){
                    whiteWon++;
                } else if (winner == Player.DRAW) {
                    draw++;
                }
                System.out.println("Black " + blackWon + ". White " + whiteWon + ". Draw " + draw);
            }

        }
    }

    private static void playSinglePlayerGame(Game game, Window window) {
        List<Move> moves = new ArrayList<>();
        Move move = new Move(-1, -1, -1, -1);

        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (move.getStartX() == -1) {
                    move.setStartX(e.getY() / Constants.ONE_RECTANGLE_SIZE);
                    move.setStartY(e.getX() / Constants.ONE_RECTANGLE_SIZE);
                    System.out.println(move);
                } else {
                    move.setEndX(e.getY() / Constants.ONE_RECTANGLE_SIZE);
                    move.setEndY(e.getX() / Constants.ONE_RECTANGLE_SIZE);
                    System.out.println(move);
                    moves.add(new Move(move));
                    move.setStartX(-1);
                    move.setStartY(-1);
                    move.setEndX(-1);
                    move.setEndY(-1);
                }
            }
        });

        window.addMouseWheelListener(e -> {
            if (!moves.isEmpty()) {
                game.performMove(new ArrayList<>(moves));
                moves.clear();
                window.repaint();
            }
        });
    }

}


