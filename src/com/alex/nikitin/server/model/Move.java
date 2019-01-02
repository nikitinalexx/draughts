package com.alex.nikitin.server.model;

import static com.alex.nikitin.server.model.Constants.BOARD_SIZE;

public class Move {
    private int startX, startY, endX, endY;

    public Move(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public boolean isValid() {
        return startX >= 0 && startX < BOARD_SIZE && startY >= 0 && startY < BOARD_SIZE
                && endX >= 0 && endX < BOARD_SIZE && endY >= 0 && endY < BOARD_SIZE;
    }

    @Override
    public String toString() {
        return "Move{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                '}';
    }
}
