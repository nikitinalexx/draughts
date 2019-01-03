package com.alex.nikitin.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static final int BOARD_SIZE = 8;
    public static final List<Checker> WHITE_CHECKERS = Collections.unmodifiableList(Arrays.asList(Checker.WHITE, Checker.WHITE_QUEEN));
    public static final List<Checker> BLACK_CHECKERS = Collections.unmodifiableList(Arrays.asList(Checker.BLACK, Checker.BLACK_QUEEN));

    public static final int ONE_RECTANGLE_SIZE = 100;
    public static final int BOARD_WIDTH = ONE_RECTANGLE_SIZE * BOARD_SIZE;

    public static final int X_OFFSET = 15;
    public static final int Y_OFFSET = 40;

    public static final int CHECKER_OFFSET = 25;
    public static final int DOT_OFFSET = 21;

    public static final int MOVES_BEFORE_DRAW = 15;
}
