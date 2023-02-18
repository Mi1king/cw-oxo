package edu.uob;

import java.util.Locale;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if (gameModel.getWinner() != null) {
            return;
        }

        // Invalid Identifier Length
        // Check the length of the identifiers
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }

        // Invalid Identifier Character

        // Check the row identifier
        char rowChar = command.toUpperCase().charAt(0);

        if (rowChar < 'A' || rowChar > 'I') {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, rowChar);
        }

        // Check the column identifier
        char colChar = command.charAt(1);
        if (colChar < '1' || colChar > '9') {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, colChar);
        }

        // Convert the identifiers to integers

        // Get the row of the cell by converting the first character of the input to an integer
        // (by subtracting the ASCII value of 'a'), since the row is represented by a letter (e.g. 'b')
        int row = rowChar - 'A';

        // Get the column of the cell by converting the second character of the input to an integer
        // (by parsing it as a substring and subtracting 1), since the column is represented by a number (e.g. '2')
        int col = colChar - '1';

        // Check if the cell is out of range
        if (row < 0 || row >= gameModel.getNumberOfRows()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row);
        }
        if (col < 0 || col >= gameModel.getNumberOfColumns()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, col);
        }

        // Check if the cell is already taken
        if (gameModel.getCellOwner(row, col) != null) {
            throw new OXOMoveException.CellAlreadyTakenException(row + 1, col + 1);
        }


        // Get the index of the current player in the game model
        int currentPlayerIndex = gameModel.getCurrentPlayerNumber();

// Get the OXOPlayer object for the current player index
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerIndex);


// Set the owner of the cell to the current player in the game model
        gameModel.setCellOwner(row, col, currentPlayer);

// Check for a win in all directions (horizontally, vertically and diagonally)
        if (checkForWin(row, col)) {
            gameModel.setWinner(currentPlayer);
        } else if (gameModel.checkForDraw()) {
            gameModel.setGameDrawn();
        } else {
            // Calculate the index of the next player in the game (by incrementing and wrapping around)
            int nextPlayerIndex = (currentPlayerIndex + 1) % gameModel.getNumberOfPlayers();

            // Update the game model with the index of the next player
            gameModel.setCurrentPlayerNumber(nextPlayerIndex);
        }
    }

    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        gameModel.removeRow();
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        gameModel.removeColumn();
    }

    public void increaseWinThreshold() {
    }

    public void decreaseWinThreshold() {
    }

    public void reset() {
        // Clear the board and reinitialise the game state to the original settings
        gameModel.reset();
        gameModel.setCurrentPlayerNumber(0);
    }

    public boolean checkForWin(int row, int col) {
        OXOPlayer currentPlayer = gameModel.getCellOwner(row, col);
        int winThreshold = gameModel.getWinThreshold();
        int numberOfRows = gameModel.getNumberOfRows();
        int numberOfColumns = gameModel.getNumberOfColumns();

        // Check for horizontal win
        int count = 1;
        int c = col - 1;
        while (c >= 0 && gameModel.getCellOwner(row, c) == currentPlayer) {
            count++;
            c--;
        }
        c = col + 1;
        while (c < numberOfColumns && gameModel.getCellOwner(row, c) == currentPlayer) {
            count++;
            c++;
        }
        if (count >= winThreshold) {
            return true;
        }

        // Check for vertical win
        count = 1;
        int r = row - 1;
        while (r >= 0 && gameModel.getCellOwner(r, col) == currentPlayer) {
            count++;
            r--;
        }
        r = row + 1;
        while (r < numberOfRows && gameModel.getCellOwner(r, col) == currentPlayer) {
            count++;
            r++;
        }
        if (count >= winThreshold) {
            return true;
        }

        // Check for diagonal win (top-left to bottom-right)
        count = 1;
        r = row - 1;
        c = col - 1;
        while (r >= 0 && c >= 0 && gameModel.getCellOwner(r, c) == currentPlayer) {
            count++;
            r--;
            c--;
        }
        r = row + 1;
        c = col + 1;
        while (r < numberOfRows && c < numberOfColumns && gameModel.getCellOwner(r, c) == currentPlayer) {
            count++;
            r++;
            c++;
        }
        if (count >= winThreshold) {
            return true;
        }

        // Check for diagonal win (bottom-left to top-right)
        count = 1;
        r = row + 1;
        c = col - 1;
        while (r < numberOfRows && c >= 0 && gameModel.getCellOwner(r, c) == currentPlayer) {
            count++;
            r++;
            c--;
        }
        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < numberOfColumns && gameModel.getCellOwner(r, c) == currentPlayer) {
            count++;
            r--;
            c++;
        }
        if (count >= winThreshold) {
            return true;
        }

        return false;
    }

}
