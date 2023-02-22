package edu.uob;

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
        if (command.length() != 2) {
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }

        // Invalid Identifier Character
        char rowChar = command.toUpperCase().charAt(0);
        char colChar = command.charAt(1);

        // Check row identifier
        if (rowChar < 'A' || rowChar > 'I') {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, rowChar);
        }

        // Check column identifier

        if (colChar < '1' || colChar > '9') {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, colChar);
        }


        // Convert the identifiers to integers
        int row = rowChar - 'A';
        int col = colChar - '1';

        // Outside Range
        if (row >= gameModel.getNumberOfRows()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, row);
        }
        if (col >= gameModel.getNumberOfColumns()) {
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, col);
        }

        // Already Taken
        if (gameModel.getCellOwner(row, col) != null) {
            throw new OXOMoveException.CellAlreadyTakenException(row + 1, col + 1);
        }


        // Set cell owner to current player
        // Get current player number
        int currentPlayerNumber = gameModel.getCurrentPlayerNumber();
        // Get current player OXOPlayer object
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(currentPlayerNumber);
        gameModel.setCellOwner(row, col, currentPlayer);

        // Check for win
        if (checkWin(row, col)) {
            gameModel.setWinner(currentPlayer);
        } else if (checkDraw()) {
            gameModel.setGameDrawn();
        }

        // Set current player to next player
        // Calculate the next player number
        int nextPlayerNumber = (currentPlayerNumber + 1) % gameModel.getNumberOfPlayers();
        gameModel.setCurrentPlayerNumber(nextPlayerNumber);
    }

    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        int last = gameModel.getNumberOfRows() - 1;
        boolean removable = true;
        for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
            OXOPlayer cellOwner = gameModel.getCellOwner(last, i);
            if (cellOwner != null) {
                removable = false;
                break;
            }
        }
        if (removable) {
            gameModel.removeRow();
        }else{
            System.out.println("Operation not allowed: There are some cells been occupied, can not remove row");
        }
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        int last = gameModel.getNumberOfColumns() - 1;
        boolean removable = true;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            OXOPlayer cellOwner = gameModel.getCellOwner(i, last);
            if (cellOwner != null) {
                removable = false;
                break;
            }
        }
        if (removable) {
            gameModel.removeColumn();
        }else{
            System.out.println("Operation not allowed: There are some cells been occupied, can not remove cloumn");
        }
    }

    public void increaseWinThreshold() {
        int threshold = gameModel.getWinThreshold();
        gameModel.setWinThreshold(threshold + 1);
    }

    public void decreaseWinThreshold() {
        int thresholdMin = 3;
        int threshold = gameModel.getWinThreshold();
        if (threshold > thresholdMin) {
            if (!gameModel.isGameInProcess() || gameModel.getWinner() != null) {
                gameModel.setWinThreshold(threshold - 1);
            }else{
                System.out.println("Operation not allowed: Game is in progress, and the threshold cannot be reduced");
            }
        }else{
            System.out.println("Operation not allowed: Minimum winning threshold reached: 3");
        }
    }

    public void reset() {
        gameModel.reset();
    }

    public boolean checkWin(int row, int col) {
        OXOPlayer currentPlayer = gameModel.getCellOwner(row, col);
        int numRows = gameModel.getNumberOfRows();
        int numColumns = gameModel.getNumberOfColumns();
        int threshold = gameModel.getWinThreshold();

        // horizontally win
        int countNum = 1;
        // check left
        int c_left = col - 1;
        while (c_left >= 0 && gameModel.getCellOwner(row, c_left) == currentPlayer) {
            countNum++;
            c_left--;
        }
        // check right
        int c_right = col + 1;
        while (c_right < numColumns && gameModel.getCellOwner(row, c_right) == currentPlayer) {
            countNum++;
            c_right++;
        }
        if (countNum >= threshold) {
            return true;
        }

        // vertically win
        countNum = 1;
        // check top
        int r_top = row - 1;
        while (r_top >= 0 && gameModel.getCellOwner(r_top, col) == currentPlayer) {
            countNum++;
            r_top--;
        }
        // check bottom
        int r_bottom = row + 1;
        while (r_bottom < numRows && gameModel.getCellOwner(r_bottom, col) == currentPlayer) {
            countNum++;
            r_bottom++;
        }
        if (countNum >= threshold) {
            return true;
        }

        // diagonally win
        // from top left to bottom right
        countNum = 1;
        r_top = row - 1;
        c_left = col - 1;
        while (r_top >= 0 && c_left >= 0 && gameModel.getCellOwner(r_top, c_left) == currentPlayer) {
            countNum++;
            r_top--;
            c_left--;
        }

        r_bottom = row + 1;
        c_right = col + 1;
        while (r_bottom < numRows && c_right < numColumns && gameModel.getCellOwner(r_bottom, c_right) == currentPlayer) {
            countNum++;
            r_bottom++;
            c_right++;
        }
        if (countNum >= threshold) {
            return true;
        }

        // from bottom left to top right
        countNum = 1;
        r_bottom = row + 1;
        c_left = col - 1;
        while (r_bottom < numRows && c_left >= 0 && gameModel.getCellOwner(r_bottom, c_left) == currentPlayer) {
            countNum++;
            r_bottom++;
            c_left--;
        }

        r_top = row - 1;
        c_right = col + 1;
        while (r_top >= 0 && c_right < numColumns && gameModel.getCellOwner(r_top, c_right) == currentPlayer) {
            countNum++;
            r_top--;
            c_right++;
        }
        return countNum >= threshold;
    }

    public boolean checkDraw() {
        for (int row = 0; row < gameModel.getNumberOfRows(); row++) {
            for (int col = 0; col < gameModel.getNumberOfColumns(); col++) {
                if (gameModel.getCellOwner(row, col) == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
