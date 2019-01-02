import com.alex.nikitin.server.MyCanvas;
import com.alex.nikitin.server.Server;
import com.alex.nikitin.server.model.Constants;
import com.alex.nikitin.server.model.Game;
import com.alex.nikitin.server.model.Move;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.alex.nikitin.server.model.Constants.*;
import static java.lang.Integer.parseInt;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Game game = Server.createNewGame();

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, BOARD_WIDTH + X_OFFSET, BOARD_WIDTH + Y_OFFSET);
        window.getContentPane().add(new MyCanvas(game));
        window.setVisible(true);

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
            game.performMove(new ArrayList<>(moves));
            moves.clear();
            window.repaint();
        });
    }

}


