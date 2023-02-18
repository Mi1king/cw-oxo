package edu.uob;

import java.util.ArrayList;

public class OXOModel {


    private ArrayList<ArrayList<OXOPlayer>> cells;
    private ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<ArrayList<OXOPlayer>>();
        for (int i = 0; i < numberOfRows; i++) {
            cells.add(new ArrayList<OXOPlayer>());
            for (int j = 0; j < numberOfColumns; j++) {
                cells.get(i).add(null);
            }
        }
        players = new ArrayList<OXOPlayer>();
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        if (cells.size() == 0) {
            return 0;
        } else {
            return cells.get(0).size();
        }
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    // Add a new column to the game board and update the cells accordingly
    public void addColumn() {
        int numberOfRows = getNumberOfRows();
        int numberOfColumns = getNumberOfColumns();
        if (numberOfColumns >= 9) {
            return; // exit the method if the maximum column limit is reached
        }
        for (int i = 0; i < numberOfRows; i++) {
            cells.get(i).add(null);
        }
    }

    // Add a new row to the game board and update the cells accordingly
    public void addRow() {
        int numberOfRows = getNumberOfRows();
        int numberOfColumns = getNumberOfColumns();
        if (numberOfRows >= 9) {
            return; // exit the method if the maximum column limit is reached
        }
        ArrayList<OXOPlayer> newRow = new ArrayList<>();
        for (int j = 0; j < numberOfColumns; j++) {
            newRow.add(null);
        }
        cells.add(newRow);
    }

    // Remove a column from the game board and update the cells accordingly
    public void removeColumn() {
        int numberOfColumns = getNumberOfColumns();
        if (numberOfColumns > 3) {
            for (int i = 0; i < getNumberOfRows(); i++) {
                cells.get(i).remove(numberOfColumns - 1);
            }
        }
    }

    // Remove a row from the game board and update the cells accordingly
    public void removeRow() {
        int numberOfRows = getNumberOfRows();
        if (numberOfRows > 3) {
            cells.remove(cells.size() - 1);
        }
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public boolean checkForDraw() {
        for (int row = 0; row < getNumberOfRows(); row++) {
            for (int col = 0; col < getNumberOfColumns(); col++) {
                if (getCellOwner(row, col) == null) {
                    return false;
                }
            }
        }
        return true;
    }
    public void reset() {
        cells = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ArrayList<OXOPlayer> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        gameDrawn = false;
        winner = null;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
