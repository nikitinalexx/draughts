import com.alex.nikitin.server.MyCanvas;
import com.alex.nikitin.server.Server;
import com.alex.nikitin.server.model.Game;
import com.alex.nikitin.server.model.Move;

import javax.swing.*;

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

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String line = scanner.nextLine();
            String[] splitted = line.split(" ");
            List<Move> moves = new ArrayList<>();
            for (int i = 0; i < splitted.length; i += 4) {
                Move move = new Move(parseInt(splitted[i]),
                        parseInt(splitted[i + 1]),
                        parseInt(splitted[i + 2]),
                        parseInt(splitted[i + 3]));
                moves.add(move);
            }
            game.performMove(moves);
            window.repaint();
        }
    }

}


