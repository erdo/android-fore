package foo.bar.example.foreui.feature.tictactoe;


import android.os.Handler;

import co.early.fore.core.Affirm;
import co.early.fore.core.WorkMode;
import co.early.fore.core.observer.ObservableImp;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.time.SystemTimeWrapper;

import static foo.bar.example.foreui.feature.tictactoe.Player.NOBODY;
import static foo.bar.example.foreui.feature.tictactoe.Player.O;
import static foo.bar.example.foreui.feature.tictactoe.Player.X;

/**
 * erdo: Most of this class is taken from here: https://github.com/ericmaxwell2003/ticTacToe
 *
 * The main alteration is to make Board Observable, I also added a NOBODY player so that
 * nothing needs to be null, and there is also a timer that regularly fires notifications during
 * a game which enables things like the jiggle reminder animation on the view
 */
public class Board extends ObservableImp {

    private final SystemTimeWrapper systemTimeWrapper;
    private final WorkMode workMode;

    private Cell[][] cells = new Cell[3][3];

    private Player winner;
    private GameState state;
    private Player currentTurn;
    private int movesMade = 0;
    private long lastMoveMadeTimestamp = 0;

    private Handler tickHandler;

    private enum GameState { IN_PROGRESS, FINISHED }

    public Board(WorkMode workMode, SystemTimeWrapper systemTimeWrapper) {
        super(WorkMode.SYNCHRONOUS);
        this.workMode = Affirm.notNull(workMode);
        this.systemTimeWrapper = Affirm.notNull(systemTimeWrapper);

        tickHandler = new Handler();

        restart();
    }

    /**
     *  Restart or start a new game, will clear the board and win status
     */
    public void restart() {
        clearCells();
        winner = NOBODY;
        currentTurn = X;
        state = GameState.IN_PROGRESS;
        movesMade = 0;
        lastMoveMadeTimestamp = systemTimeWrapper.currentTimeMillis();
        notifyObservers();
    }

    /**
     * Mark the current row for the player who's current turn it is.
     * Will perform no-op if the arguments are out of range or if that position is already played.
     * Will also perform a no-op if the game is already over.
     *
     * @param row 0..2
     * @param col 0..2
     * @return the player that moved or null if we did not move anything.
     *
     */
    public Player mark( int row, int col ) {

        Player playerThatMoved = null;

        if(isValid(row, col)) {

            movesMade++;
            lastMoveMadeTimestamp = systemTimeWrapper.currentTimeMillis();

            cells[row][col].setValue(currentTurn);
            playerThatMoved = currentTurn;

            if(isWinningMoveByPlayer(currentTurn, row, col)) {
                state = GameState.FINISHED;
                winner = currentTurn;
                lastMoveMadeTimestamp = 0;
            } else if (movesMade > 8) {
                state = GameState.FINISHED;
                winner = NOBODY;
                lastMoveMadeTimestamp = 0;
            }else {
                // flip the current turn and continue
                flipCurrentTurn();
            }

        }

        notifyObservers();

        return playerThatMoved;
    }


    public Player valueAtCell(int row, int col) {
        return cells[row][col].getValue();
    }

    public Player getWinner() {
        return winner;
    }

    public Player getNextPlayer() {
        return currentTurn;
    }

    public int getMovesMade() {
        return movesMade;
    }

    public boolean isGameInProgress() {
        return state == GameState.IN_PROGRESS;
    }

    public long timeSinceLastMove(){
        return lastMoveMadeTimestamp == 0 ? 0 : systemTimeWrapper.currentTimeMillis() - lastMoveMadeTimestamp;
    }

    private void clearCells() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private boolean isValid(int row, int col ) {
        if( state == GameState.FINISHED ) {
            return false;
        } else if( isOutOfBounds(row) || isOutOfBounds(col) ) {
            return false;
        } else if( isCellValueAlreadySet(row, col) ) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isOutOfBounds(int idx) {
        return idx < 0 || idx > 2;
    }

    private boolean isCellValueAlreadySet(int row, int col) {
        return cells[row][col].getValue() != NOBODY;
    }


    /**
     * Algorithm adapted from http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe.html
     * @param player
     * @param currentRow
     * @param currentCol
     * @return true if <code>player</code> who just played the move at the <code>currentRow</code>, <code>currentCol</code>
     *              has a tic tac toe.
     */
    private boolean isWinningMoveByPlayer(Player player, int currentRow, int currentCol) {

        return (cells[currentRow][0].getValue() == player         // 3-in-the-row
                && cells[currentRow][1].getValue() == player
                && cells[currentRow][2].getValue() == player
                || cells[0][currentCol].getValue() == player      // 3-in-the-column
                && cells[1][currentCol].getValue() == player
                && cells[2][currentCol].getValue() == player
                || currentRow == currentCol            // 3-in-the-diagonal
                && cells[0][0].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][2].getValue() == player
                || currentRow + currentCol == 2    // 3-in-the-opposite-diagonal
                && cells[0][2].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][0].getValue() == player);
    }

    private void flipCurrentTurn() {
        currentTurn = currentTurn == X ? O : X;
    }


    // erdo: the code below fires observers periodically whenever there is an observer
    // present - this is only necessary to drive the kind of views that want
    // continual updates - in this case the view jiggles if there has been
    // no move made after x seconds. This implementation using our observable
    // pattern lets everything work across screen orientation changes

    private void tick(){
        if (hasObservers() && workMode == WorkMode.ASYNCHRONOUS){
            notifyObservers();
            tickHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tick();
                }
            }, 1000);//the timing doesn't matter here, a bit late is fine
        }
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
        tick();
    }

    @Override
    public synchronized void removeObserver(Observer observer) {
        super.removeObserver(observer);
        tickHandler.removeCallbacksAndMessages(null);
    }


}
