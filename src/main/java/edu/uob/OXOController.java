package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
//        int currentPlayerIndex = gameModel.getCurrentPlayerNumber();
//        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerIndex);
//        String input = command.toLowerCase();
//        int row = input.charAt(0) - 'a';
//        int col = Integer.parseInt(input.substring(1))-1;
//        gameModel.setCellOwner(row,col,currentPlayer);
//        int nextPlayerIndex = (currentPlayerIndex+1) % gameModel.getNumberOfPlayers();
//        gameModel.setCurrentPlayerNumber(nextPlayerIndex);

        // Get the index of the current player in the game model
        int currentPlayerIndex = gameModel.getCurrentPlayerNumber();

// Get the OXOPlayer object for the current player index
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerIndex);

// Convert the command input to lowercase
        String input = command.toLowerCase();

// Get the row of the cell by converting the first character of the input to an integer
// (by subtracting the ASCII value of 'a'), since the row is represented by a letter (e.g. 'b')
        int row = input.charAt(0) - 'a';

// Get the column of the cell by converting the second character of the input to an integer
// (by parsing it as a substring and subtracting 1), since the column is represented by a number (e.g. '2')
        int col = Integer.parseInt(input.substring(1)) - 1;

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
