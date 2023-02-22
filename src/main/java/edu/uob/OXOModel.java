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
        cells = new ArrayList<>();
        for (int i = 0; i < numberOfRows; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < numberOfColumns; j++) {
                cells.get(i).add(null);
            }
        }
        players = new ArrayList<>();
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
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
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

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addColumn() {
        if (getNumberOfColumns() >= 9) {
            return;
        }
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).add(null);
        }
    }

    public void addRow() {
        if (getNumberOfRows() >= 9) {
            return;
        }
        ArrayList<OXOPlayer> row = new ArrayList<>();
        for (int j = 0; j < getNumberOfColumns(); j++) {
            row.add(null);
        }
        cells.add(row);
    }

    public void removeColumn() {
        int numColumns = getNumberOfColumns();
        if (numColumns > 1) {
            for (int i = 0; i < getNumberOfRows(); i++) {
                cells.get(i).remove(numColumns - 1);
            }
        }
    }

    public void removeRow() {
        if (getNumberOfRows() > 1) {
            cells.remove(cells.size() - 1);
        }
    }

    public boolean isGameInProcess() {
        for (ArrayList<OXOPlayer> cell : cells) {
            for (OXOPlayer oxoPlayer : cell) {
                if (oxoPlayer != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        int numRows = getNumberOfRows();
        int numColumns = getNumberOfColumns();
        cells = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            ArrayList<OXOPlayer> row = new ArrayList<>();
            for (int j = 0; j < numColumns; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        currentPlayerNumber = 0;
        winner = null;
        gameDrawn = false;
    }
}
