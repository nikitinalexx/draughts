package com.alex.nikitin.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alex.nikitin.server.model.Checker.*;
import static com.alex.nikitin.server.model.Constants.BOARD_SIZE;

public class ChangeableBoard {
    private Checker[][] position;
    private boolean isWhiteTurn;
    private boolean reversedForTest;
    private boolean generatedMovesShouldBeReversed = false;
    private ChangeableBoard parent;
    private int numberInSequence;

    public ChangeableBoard() {
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
        numberInSequence = 0;
    }

    public ChangeableBoard(ChangeableBoard source) {
        position = new Checker[8][];
        for (int i = 0; i < BOARD_SIZE; i++) {
            position[i] = Arrays.copyOf(source.position[i], BOARD_SIZE);
        }
        isWhiteTurn = source.isWhiteTurn;
        reversedForTest = source.reversedForTest;
        generatedMovesShouldBeReversed = source.generatedMovesShouldBeReversed;
        parent = source;
        numberInSequence = source.numberInSequence + 1;
    }

    private ChangeableBoard(ChangeableBoard source, boolean reversedForTest) {
        position = new Checker[8][];
        for (int i = 0; i < BOARD_SIZE; i++) {
            position[i] = Arrays.copyOf(source.position[i], BOARD_SIZE);
        }

        if (reversedForTest) {
            revertPositions(source, this);
            this.reversedForTest = true;
        }
        isWhiteTurn = source.isWhiteTurn;
    }

    public ChangeableBoard getReversedBoard() {
        ChangeableBoard target = new ChangeableBoard(this);
        revertPositions(this, target);
        target.reversedForTest = false;
        target.isWhiteTurn = true;
        target.generatedMovesShouldBeReversed = true;
        revertColours(target);
        return target;
    }

    private void revertPositions(ChangeableBoard source, ChangeableBoard target) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            target.position[BOARD_SIZE - 1 - i] = Arrays.copyOf(source.position[i], BOARD_SIZE);
            for (int j = 0; j < BOARD_SIZE / 2; j++) {
                Checker temp = target.position[BOARD_SIZE - 1 - i][j];
                target.position[BOARD_SIZE - 1 - i][j] = target.position[BOARD_SIZE - 1 - i][BOARD_SIZE - 1 - j];
                target.position[BOARD_SIZE - 1 - i][BOARD_SIZE - 1 - j] = temp;
            }
        }
    }

    private static void revertColours(ChangeableBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                switch(board.position[i][j]) {
                    case BLACK:
                        board.position[i][j] = WHITE;
                        break;
                    case BLACK_QUEEN:
                        board.position[i][j] = WHITE_QUEEN;
                        break;
                    case WHITE:
                        board.position[i][j] = BLACK;
                        break;
                    case WHITE_QUEEN:
                        board.position[i][j] = BLACK_QUEEN;
                        break;
                }
            }
        }
    }

    public Checker[][] getPosition() {
        return position;
    }

    public List<List<Move>> getPossibleMoves() {
        ChangeableBoard reversedBoard;
        if (isWhiteTurn()) {
            reversedBoard = this;
        } else {
            reversedBoard = this.getReversedBoard();
        }

        ChangeableBoard board = new ChangeableBoard(reversedBoard);

        List<List<Move>> possibleMoves = new ArrayList<>();

        if (shouldKill(board)) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    List<List<Move>> usualKillMoves = getKillMoves(board, i, j);
                    possibleMoves.addAll(usualKillMoves);
                }
            }
        } else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Move move;
                    if (board.getPosition()[i][j] == WHITE) {
                        move = new Move(i, j, i - 1, j -1);
                        if (move.isValid() && isValidNonKillMove(board, move)) {
                            possibleMoves.add(Collections.singletonList(move));
                        }
                        move = new Move(i, j, i - 1, j + 1);
                        if (move.isValid() && isValidNonKillMove(board, move)) {
                            possibleMoves.add(Collections.singletonList(move));
                        }
                    }

                    if (board.getPosition()[i][j] == WHITE_QUEEN) {
                        for (int k = 1; k < BOARD_SIZE - 1; k++) {
                            move = new Move(i, j, i - 1 - k, j - 1 - k);
                            if (move.isValid() && isValidNonKillMove(board, move)) {
                                possibleMoves.add(Collections.singletonList(move));
                            }
                            move = new Move(i, j, i - 1 - k, j + 1 + k);
                            if (move.isValid() && isValidNonKillMove(board, move)) {
                                possibleMoves.add(Collections.singletonList(move));
                            }
                            move = new Move(i, j, i + 1 + k, j - 1 - k);
                            if (move.isValid() && isValidNonKillMove(board, move)) {
                                possibleMoves.add(Collections.singletonList(move));
                            }
                            move = new Move(i, j, i + 1 + k, j + 1 + k);
                            if (move.isValid() && isValidNonKillMove(board, move)) {
                                possibleMoves.add(Collections.singletonList(move));
                            }
                        }
                    }

                }
            }
        }

        if (board.generatedMovesShouldBeReversed) {
            possibleMoves = possibleMoves.stream().map(BoardHelper::reversedMoves).collect(Collectors.toList());
        }

        return possibleMoves;
    }

    public static boolean shouldKill(ChangeableBoard newBoard) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (newBoard.isWhiteTurn && newBoard.position[i][j] == WHITE || !newBoard.isWhiteTurn && newBoard.position[i][j] == BLACK) {
                    List<Move> possibleKillMoves = getUsualCheckerPossibleKillMoves(i, j);
                    for (Move possibleKillMove : possibleKillMoves) {
                        if (isValidKillMove(newBoard, possibleKillMove)) {
                            return true;
                        }
                    }
                }
                if (newBoard.isWhiteTurn && newBoard.position[i][j] == WHITE_QUEEN || !newBoard.isWhiteTurn && newBoard.position[i][j] == BLACK_QUEEN) {
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


    public ChangeableBoard performMove(List<Move> moves) {
        if (!movesAreValid(moves)) {
            System.out.println("Move is not valid");
            System.out.println(Arrays.deepToString(position));
            throw new RuntimeException();
        }

        return BoardFactory.makeAMove(this, moves);
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
        ChangeableBoard newBoard = new ChangeableBoard(this, needReversed);

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

        if (enteredKillingMode && shouldKillSpecificChecker(newBoard, moves.get(moves.size() - 1))) {
            System.out.println("You can kill more checkers");
            return false;
        }

        return true;
    }

    public static void applyMoveToBoard(ChangeableBoard board, Move move) {
        Checker checker = board.position[move.getStartX()][move.getStartY()];

        if (move.getEndX() == 0 && checker == Checker.WHITE) {
            checker = Checker.WHITE_QUEEN;
        }

        if ((board.reversedForTest && move.getEndX() == 0 || !board.reversedForTest && move.getEndX() == BOARD_SIZE - 1) && checker == Checker.BLACK) {
            checker = Checker.BLACK_QUEEN;
        }

        board.position[move.getStartX()][move.getStartY()] = Checker.NONE;
        board.position[move.getEndX()][move.getEndY()] = checker;

        boolean iIsPlus = (move.getStartX() - move.getEndX()) < 0;
        boolean jIsPlus = (move.getStartY() - move.getEndY()) < 0;
        for (int i = move.getStartX() + (iIsPlus ? 1 : -1), j = move.getStartY() + (jIsPlus ? 1 : -1);
             (iIsPlus && i < move.getEndX() || !iIsPlus && i > move.getEndX()) && (jIsPlus && j < move.getEndY() || !jIsPlus && j > move.getEndY());
             i += (iIsPlus ? 1 : -1), j += (jIsPlus ? 1 : -1)) {
            board.position[i][j] = Checker.NONE;
        }
    }

    private boolean shouldKillSpecificChecker(ChangeableBoard newBoard, Move move) {
        Checker checker = newBoard.position[move.getEndX()][move.getEndY()];
        if (newBoard.isWhiteTurn && checker == WHITE || !newBoard.isWhiteTurn && checker == BLACK) {
            List<Move> possibleKillMoves = getUsualCheckerPossibleKillMoves(move.getEndX(), move.getEndY());
            for (Move possibleKillMove : possibleKillMoves) {
                if (isValidKillMove(newBoard, possibleKillMove)) {
                    return true;
                }
            }
        }
        if (newBoard.isWhiteTurn && checker == WHITE_QUEEN || !newBoard.isWhiteTurn && checker == BLACK_QUEEN) {
            List<Move> possibleKillMoves = getQueenCheckerPossibleKillMoves(move.getEndX(), move.getEndY());
            for (Move possibleKillMove : possibleKillMoves) {
                if (isValidKillMove(newBoard, possibleKillMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<Move> getUsualCheckerPossibleKillMoves(int x, int y) {
        Move first = new Move(x, y, x - 2, y - 2);
        Move second = new Move(x, y, x - 2, y + 2);
        Move third = new Move(x, y, x + 2, y - 2);
        Move fourth = new Move(x, y, x + 2, y + 2);

        return Stream.of(first, second, third, fourth).filter(Move::isValid).collect(Collectors.toList());
    }

    private static List<Move> getQueenCheckerPossibleKillMoves(int x, int y) {
        List<Move> result = new ArrayList<>();

        for (int i = 1; i < BOARD_SIZE - 1; i++) {
            result.add(new Move(x, y, x - 1 - i, y - 1 - i));
            result.add(new Move(x, y, x - 1 - i, y + 1 + i));
            result.add(new Move(x, y, x + 1 + i, y - 1 - i));
            result.add(new Move(x, y, x + 1 + i, y + 1 + i));
        }

        return result.stream().filter(Move::isValid).collect(Collectors.toList());
    }

    private static boolean isValidKillMove(ChangeableBoard board, Move move) {


        boolean isQueen = board.getChecker(move) == BLACK_QUEEN || board.getChecker(move) == WHITE_QUEEN;
        if (!isQueen && (Math.abs(move.getStartX() - move.getEndX()) != 2 || Math.abs(move.getStartY() - move.getEndY()) != 2)) {
            return false;
        }

        if (board.position[move.getEndX()][move.getEndY()] != Checker.NONE) {
            return false;
        }

        return getNumberOfKills(board, move) == 1;
    }

    private static boolean isValidNonKillMove(ChangeableBoard board, Move move) {
        boolean isQueen = board.getChecker(move) == BLACK_QUEEN || board.getChecker(move) == WHITE_QUEEN;
        if (!isQueen && (Math.abs(move.getStartX() - move.getEndX()) != 1 || Math.abs(move.getStartY() - move.getEndY()) != 1)) {
            return false;
        }

        if (board.position[move.getEndX()][move.getEndY()] != Checker.NONE) {
            return false;
        }

        return getNumberOfAnyKills(board, move) == 0;
    }

    private static int getNumberOfKills(ChangeableBoard board, Move move) {
        int count = 0;
        boolean iIsPlus = (move.getStartX() - move.getEndX()) < 0;
        boolean jIsPlus = (move.getStartY() - move.getEndY()) < 0;
        for (int i = move.getStartX() + (iIsPlus ? 1 : -1), j = move.getStartY() + (jIsPlus ? 1 : -1);
             (iIsPlus && i < move.getEndX() || !iIsPlus && i > move.getEndX()) && (jIsPlus && j < move.getEndY() || !jIsPlus && j > move.getEndY());
             i += (iIsPlus ? 1 : -1), j += (jIsPlus ? 1 : -1)) {
            Checker checker = board.position[i][j];
            if (board.isWhiteTurn) {
                if (checker == WHITE || checker == WHITE_QUEEN) {
                    return -1;
                } else if (checker == BLACK || checker == BLACK_QUEEN) {
                    count++;
                }
            } else {
                if (checker == BLACK || checker == BLACK_QUEEN) {
                    return -1;
                } else if (checker == WHITE || checker == WHITE_QUEEN) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int getNumberOfAnyKills(ChangeableBoard board, Move move) {
        int count = 0;
        boolean iIsPlus = (move.getStartX() - move.getEndX()) < 0;
        boolean jIsPlus = (move.getStartY() - move.getEndY()) < 0;
        for (int i = move.getStartX() + (iIsPlus ? 1 : -1), j = move.getStartY() + (jIsPlus ? 1 : -1);
             (iIsPlus && i < move.getEndX() || !iIsPlus && i > move.getEndX()) && (jIsPlus && j < move.getEndY() || !jIsPlus && j > move.getEndY());
             i += (iIsPlus ? 1 : -1), j += (jIsPlus ? 1 : -1)) {
            if (board.position[i][j] != NONE) {
                count++;
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

    private List<List<Move>> getKillMoves(ChangeableBoard board, int i, int j) {
        List<List<Move>> possibleMoves = new ArrayList<>();
        Move move;
        if (board.position[i][j] == WHITE) {
            move = new Move(i, j, i - 2, j -2);
            if (move.isValid() && isValidKillMove(board, move)) {
                getKillMovesRecursively(board, move, possibleMoves, i - 2, j - 2);
            }
            move = new Move(i, j, i - 2, j + 2);
            if (move.isValid() && isValidKillMove(board, move)) {
                getKillMovesRecursively(board, move, possibleMoves, i - 2, j + 2);
            }
            move = new Move(i, j, i + 2, j - 2);
            if (move.isValid() && isValidKillMove(board, move)) {
                getKillMovesRecursively(board, move, possibleMoves, i + 2, j - 2);
            }
            move = new Move(i, j, i + 2, j + 2);
            if (move.isValid() && isValidKillMove(board, move)) {
                getKillMovesRecursively(board, move, possibleMoves, i + 2, j + 2);
            }
        }
        if (board.position[i][j] == WHITE_QUEEN) {
            for (int k = 1; k < BOARD_SIZE - 1; k++) {
                move = new Move(i, j, i - 1 - k, j - 1 - k);
                if (move.isValid() && isValidKillMove(board, move)) {
                    getKillMovesRecursively(board, move, possibleMoves, i - 1 - k, j - 1 - k);
                }
                move = new Move(i, j, i - 1 - k, j + 1 + k);
                if (move.isValid() && isValidKillMove(board, move)) {
                    getKillMovesRecursively(board, move, possibleMoves, i - 1 - k, j + 1 + k);
                }
                move = new Move(i, j, i + 1 + k, j - 1 - k);
                if (move.isValid() && isValidKillMove(board, move)) {
                    getKillMovesRecursively(board, move, possibleMoves, i + 1 + k, j - 1 - k);
                }
                move = new Move(i, j, i + 1 + k, j + 1 + k);
                if (move.isValid() && isValidKillMove(board, move)) {
                    getKillMovesRecursively(board, move, possibleMoves, i + 1 + k, j + 1 + k);
                }
            }
        }
        return possibleMoves;
    }

    private void getKillMovesRecursively(ChangeableBoard board, Move move, List<List<Move>> moves, int x, int y) {
        List<Move> currentMoves = Collections.singletonList(move);

        ChangeableBoard anotherBoard = new ChangeableBoard(board);
        applyMoveToBoard(anotherBoard, move);
        List<List<Move>> anotherResult = getKillMoves(anotherBoard, x, y);
        if (!anotherResult.isEmpty()) {
            for (List<Move> anotherList : anotherResult) {
                List<Move> result = new ArrayList<>(currentMoves);
                result.addAll(anotherList);
                moves.add(result);
            }
        } else {
            moves.add(currentMoves);
        }

    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void setWhiteTurn(boolean isWhiteTurn) {
        this.isWhiteTurn = isWhiteTurn;
    }

    public ChangeableBoard getParent() {
        return parent;
    }

    public int getNumberInSequence() {
        return numberInSequence;
    }

}