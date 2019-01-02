package com.alex.nikitin.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alex.nikitin.server.model.Checker.*;
import static com.alex.nikitin.server.model.Constants.BOARD_SIZE;

public class Board {
    private Checker[][] position;
    private boolean isWhiteTurn;

    public Board() {
        isWhiteTurn = true;
        position = new Checker[][]{
                {NONE, BLACK, NONE, BLACK, NONE, BLACK, NONE, BLACK},
                {BLACK, NONE, BLACK, NONE, BLACK, NONE, BLACK, NONE},
                {NONE, BLACK, NONE, BLACK, NONE, BLACK, NONE, BLACK},
                {NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE},
                {NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE},
                {WHITE, NONE, WHITE, NONE, WHITE, NONE, WHITE, NONE},
                {NONE, WHITE, NONE, WHITE, NONE, WHITE, NONE, WHITE},
                {WHITE, NONE, WHITE, NONE, WHITE, NONE, WHITE, NONE}
        };
    }

    public Board(Board board) {
        this(board, false);
        isWhiteTurn = !board.isWhiteTurn;
    }

    public Board(Board board, boolean reversed) {
        position = new Checker[8][];
        if (!reversed) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                position[i] = Arrays.copyOf(board.position[i], BOARD_SIZE);
            }
        } else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                position[BOARD_SIZE - 1 - i] = Arrays.copyOf(board.position[i], BOARD_SIZE);
                for (int j = 0; j < BOARD_SIZE / 2; j++) {
                    Checker temp = position[BOARD_SIZE - 1 - i][j];
                    position[BOARD_SIZE - 1 - i][j] = position[BOARD_SIZE - 1 - i][BOARD_SIZE - 1 - j];
                    position[BOARD_SIZE - 1 - i][BOARD_SIZE - 1 - j] = temp;
                }
            }
        }
    }

    public Checker[][] getPosition() {
        return position;
    }

    public boolean anyLeft(List<Checker> validCheckers) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (validCheckers.contains(position[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public Board performMove(List<Move> moves) {
        if (!movesAreValid(moves)) {
            System.out.println("Move is not valid");
            return this;
        }
        Board newBoard = new Board(this);

        moves.forEach((Move move) -> applyMoveToBoard(newBoard, move));

        return newBoard;
    }

    private boolean movesAreValid(List<Move> moves) {
        for (Move move : moves) {
            if (!move.isValid()) {
                System.out.println("Incorrect final coordinates " + move);
                return false;
            }
        }
        Move firstMove = moves.get(0);

        if (isWhiteTurn && getChecker(firstMove) != Checker.WHITE && getChecker(firstMove) != Checker.WHITE_QUEEN) {
            System.out.println("It is white turn but you're trying to move black " + firstMove);
            return false;
        }

        if (!isWhiteTurn && getChecker(firstMove) != Checker.BLACK && getChecker(firstMove) != Checker.BLACK_QUEEN) {
            System.out.println("It is black turn but you're trying to move white " + firstMove);
            return false;
        }

        for (int i = 0; i < moves.size() - 1; i++) {
            Move currentMove = moves.get(i);
            Move nextMove = moves.get(i + 1);
            if (currentMove.getEndX() != nextMove.getStartX() || currentMove.getEndY() != nextMove.getStartY()) {
                System.out.println("Start " + currentMove + " and end coordinates don't match " + nextMove);
                return false;
            }
        }

        boolean needReversed = !isWhiteTurn;
        Board newBoard = new Board(this, needReversed);
        if (needReversed) {
            moves = reversedMoves(moves);
        }

        boolean enteredKillingMode = false;
        for (Move move : moves) {
            if (enteredKillingMode) {
                if (!isValidKillMove(newBoard, move)) {
                    System.out.println("You're in a killing mode but not going to kill someone " + move);
                    return false;
                } else {
                    applyMoveToBoard(newBoard, move);
                }
            } else {
                if (shouldKill(newBoard)) {
                    if (!isValidKillMove(newBoard, move)) {
                        System.out.println("You should kill, but you're not going to " + move);
                        return false;
                    } else {
                        enteredKillingMode = true;
                        applyMoveToBoard(newBoard, move);
                    }
                } else if (!isValidNonKillMove(newBoard, move) || moves.size() != 1) {
                    if (moves.size() != 1) {
                        System.out.println("If you simply move you should move only once");
                    } else {
                        System.out.println("You should not kill and your move is not a simple move " + move);
                    }
                    return false;
                }
            }
        }

        return true;
    }

    private void applyMoveToBoard(Board board, Move move) {
        Checker checker = board.position[move.getStartX()][move.getStartY()];
        if (move.getEndX() == 0) {
            if (checker == Checker.WHITE) {
                checker = Checker.WHITE_QUEEN;
            }
            if (checker == Checker.BLACK) {
                checker = Checker.BLACK_QUEEN;
            }
        }

        board.position[move.getStartX()][move.getStartY()] = Checker.NONE;
        board.position[move.getEndX()][move.getEndY()] = checker;



        int minX = Math.min(move.getStartX(), move.getEndX());
        int maxX = Math.max(move.getStartX(), move.getEndX());

        int minY = Math.min(move.getStartY(), move.getEndY());
        int maxY = Math.max(move.getStartY(), move.getEndY());

        for (int i = minX + 1; i < maxX; i++) {
            for (int j = minY + 1; j < maxY; j++) {
                board.position[i][j] = Checker.NONE;
            }
        }
    }

    private boolean isValidKillMove(Board board, Move move) {
        int minX = Math.min(move.getStartX(), move.getEndX());
        int maxX = Math.max(move.getStartX(), move.getEndX());

        int minY = Math.min(move.getStartY(), move.getEndY());
        int maxY = Math.max(move.getStartY(), move.getEndY());

        boolean isQueen = getChecker(move) == BLACK_QUEEN || getChecker(move) == WHITE_QUEEN;
        if (!isQueen && (maxX - minX != 2 || maxY - minY != 2)) {
            return false;
        }

        if (board.position[move.getEndX()][move.getEndY()] != Checker.NONE) {
            return false;
        }

        return getNumberOfKills(board, minX, minY, maxX, maxY) == 1;
    }

    private boolean shouldKill(Board newBoard) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isWhiteTurn && newBoard.position[i][j] == WHITE || !isWhiteTurn && newBoard.position[i][j] == BLACK) {
                    List<Move> possibleKillMoves = getUsualCheckerPossibleKillMoves(i, j);
                    for (Move possibleKillMove : possibleKillMoves) {
                        if (isValidKillMove(newBoard, possibleKillMove)) {
                            return true;
                        }
                    }
                }
                if (isWhiteTurn && newBoard.position[i][j] == WHITE_QUEEN || !isWhiteTurn && newBoard.position[i][j] == BLACK_QUEEN) {
                    List<Move> possibleKillMoves = getQueenCheckerPossibleKillMoves(i, j);
                    for (Move possibleKillMove : possibleKillMoves) {
                        if (isValidKillMove(newBoard, possibleKillMove)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<Move> getUsualCheckerPossibleKillMoves(int x, int y) {
        Move first = new Move(x, y, x - 2, y - 2);
        Move second = new Move(x, y, x - 2, y + 2);
        Move third = new Move(x, y, x + 2, y - 2);
        Move fourth = new Move(x, y, x + 2, y + 2);

        return Stream.of(first, second, third, fourth).filter(Move::isValid).collect(Collectors.toList());
    }

    private List<Move> getQueenCheckerPossibleKillMoves(int x, int y) {
        List<Move> result = new ArrayList<>();

        for (int i = 1; i < BOARD_SIZE - 1; i++) {
            result.add(new Move(x, y, x - 1 - i, y - 1 - i));
            result.add(new Move(x, y, x - 1 - i, y + 1 + i));
            result.add(new Move(x, y, x + 1 + i, y - 1 - i));
            result.add(new Move(x, y, x + 1 + i, y + 1 + i));
        }

        return result.stream().filter(Move::isValid).collect(Collectors.toList());
    }

    private boolean isValidNonKillMove(Board board, Move move) {
        int minX = Math.min(move.getStartX(), move.getEndX());
        int maxX = Math.max(move.getStartX(), move.getEndX());

        int minY = Math.min(move.getStartY(), move.getEndY());
        int maxY = Math.max(move.getStartY(), move.getEndY());

        boolean isQueen = getChecker(move) == BLACK_QUEEN || getChecker(move) == WHITE_QUEEN;
        if (!isQueen && (move.getStartX() - move.getEndX() != 1 || maxY - minY != 1)) {
            return false;
        }

        if (board.position[move.getEndX()][move.getEndY()] != Checker.NONE) {
            return false;
        }

        return getNumberOfKills(board, minX, minY, maxX, maxY) == 0;
    }

    private int getNumberOfKills(Board board, int minX, int minY, int maxX, int maxY) {
        int count = 0;
        for (int i = minX + 1; i < maxX; i++) {
            for (int j = minY + 1; j < maxY; j++) {
                if (isWhiteTurn && (board.position[i][j] == BLACK || board.position[i][j] == BLACK_QUEEN)) {
                    count++;
                }
                if (!isWhiteTurn && (board.position[i][j] == WHITE || board.position[i][j] == WHITE_QUEEN)) {
                    count++;
                }
            }
        }

        return count;
    }

    private Checker getChecker(Move move) {
        return position[move.getStartX()][move.getStartY()];
    }

    private List<Move> reversedMoves(List<Move> moves) {
        return moves.stream().map((Move move) ->
                new Move(
                        BOARD_SIZE - move.getStartX() - 1, BOARD_SIZE - move.getStartY() - 1,
                        BOARD_SIZE - move.getEndX() - 1, BOARD_SIZE - move.getEndY() - 1)
        ).collect(Collectors.toList());
    }

}
/*


5 0 4 1
2 1 3 2
 */
