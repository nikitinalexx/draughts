package com.alex.nikitin.server;

import com.alex.nikitin.server.model.Board;
import com.alex.nikitin.server.model.Checker;

import javax.swing.*;
import java.awt.*;

import static com.alex.nikitin.server.model.Constants.*;

public class MyCanvas extends JComponent {
    private Game game;

    public MyCanvas(Game game) {
        this.game = game;
    }

    public void paint(Graphics g) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                g.drawRect(i * ONE_RECTANGLE_SIZE, j * ONE_RECTANGLE_SIZE, ONE_RECTANGLE_SIZE, ONE_RECTANGLE_SIZE);
            }
        }
        Board board = game.getCurrentBoard();
        Checker[][] positions = board.getPosition();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Color color = null;
                if (positions[i][j] == Checker.WHITE || positions[i][j] == Checker.WHITE_QUEEN) {
                    color = Color.PINK;
                } else if (positions[i][j] == Checker.BLACK || positions[i][j] == Checker.BLACK_QUEEN) {
                    color = Color.BLACK;
                }

                boolean isQueen = false;
                if (positions[i][j] == Checker.WHITE_QUEEN || positions[i][j] == Checker.BLACK_QUEEN) {
                    isQueen = true;
                }

                if (color != null) {
                    g.setColor(color);
                    g.fillOval(j * ONE_RECTANGLE_SIZE + CHECKER_OFFSET, i * ONE_RECTANGLE_SIZE + CHECKER_OFFSET, 50, 50);
                }
                if (isQueen) {
                    g.setColor(Color.RED);
                    g.fillOval(j * ONE_RECTANGLE_SIZE + CHECKER_OFFSET + DOT_OFFSET,
                            i * ONE_RECTANGLE_SIZE + CHECKER_OFFSET + DOT_OFFSET,
                            10,
                            10
                    );
                }

            }
        }
    }
}
