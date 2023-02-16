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

// Calculate the index of the next player in the game (by incrementing and wrapping around)
        int nextPlayerIndex = (currentPlayerIndex + 1) % gameModel.getNumberOfPlayers();

// Update the game model with the index of the next player
        gameModel.setCurrentPlayerNumber(nextPlayerIndex);
    }

    public void addRow() {
    }

    public void removeRow() {
    }

    public void addColumn() {
    }

    public void removeColumn() {
    }

    public void increaseWinThreshold() {
    }

    public void decreaseWinThreshold() {
    }

    public void reset() {
    }
}
