package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
    private OXOModel model;
    private OXOController controller;

    // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
    // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testBasicMoveTaking() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());

        sendCommandToController("a1");

        String failedTestComment = "Cell a1 wasn't claimed by the first player";
        assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
    }


    // Multiple player tests
    @Test
    void testMulPlayer() {
        OXOPlayer player = new OXOPlayer('G');
        model.addPlayer(player);
        assertEquals(3, model.getNumberOfPlayers());

        // Make a bunch of moves for the 3 players
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 3rd player
        String failedTestComment = "The number of players is not 3";
        assertEquals(player.getPlayingLetter(), model.getCellOwner(2, 0).getPlayingLetter(), failedTestComment);
    }

    @Test
    void testNoWinnerAtBegin() {
        String failedTestComment = "No winner at beginning";
        assertNull(model.getWinner(), failedTestComment);
    }

    @Test
    void testNotDrawAtBegin() {
        String failedTestComment = "The game should not be a draw at beginning";
        assertFalse(model.isGameDrawn(), failedTestComment);
    }

    // Draw tests
    @Test
    void testBasicDraw() {
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        String failedTestComment = "The game should be a draw";
        assertTrue(model.isGameDrawn(), failedTestComment);
    }

    @Test
    void test43Draw() {
        controller.addRow();
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        sendCommandToController("d1"); // 2nd player
        sendCommandToController("d2"); // 1st player
        sendCommandToController("d3"); // 2nd player

        String failedTestComment = "The game should be a draw";
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    @Test
    void test34Draw() {
        controller.addColumn();
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        sendCommandToController("a4"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("c4"); // 2nd player

        String failedTestComment = "The game should be a draw";
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    @Test
    void test44Draw() {
        controller.addColumn();
        controller.addRow();
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        sendCommandToController("a4"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("c4"); // 2nd player

        sendCommandToController("d4"); // 1st player
        sendCommandToController("d1"); // 2nd player
        sendCommandToController("d2"); // 1st player
        sendCommandToController("d3"); // 2nd player

        String failedTestComment = "The game should be a draw";
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    @Test
    void test44ThresholdDraw() {
        controller.addColumn();
        controller.addRow();
        controller.increaseWinThreshold();

        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        sendCommandToController("a4"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("c4"); // 2nd player

        sendCommandToController("d1"); // 1st player
        sendCommandToController("d4"); // 2nd player
        sendCommandToController("d2"); // 1st player
        sendCommandToController("d3"); // 2nd player

        String failedTestComment = "The game should be a draw";
        assertNull(model.getWinner(),failedTestComment);
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    @Test
    void testMulDraw() {
        model.addPlayer(new OXOPlayer('G'));
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 3rd player
        sendCommandToController("a2"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 3rd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("a3"); // 3rd player

        String failedTestComment = "The game should be a draw";
        assertNull(model.getWinner(),failedTestComment);
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    @Test
    void testMulThresholdDraw() {
        controller.addColumn();
        controller.addRow();
        controller.increaseWinThreshold();

        model.addPlayer(new OXOPlayer('G'));
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c1"); // 3rd player
        sendCommandToController("a2"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 3rd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("a3"); // 3rd player

        sendCommandToController("a4"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("c4"); // 2nd player

        sendCommandToController("d1"); // 1st player
        sendCommandToController("d4"); // 2nd player
        sendCommandToController("d2"); // 1st player
        sendCommandToController("d3"); // 2nd player

        String failedTestComment = "The game should be a draw";
        assertNull(model.getWinner(),failedTestComment);
        assertTrue(model.isGameDrawn(),failedTestComment);
    }

    //// Win tests
    // 1. basic win
    @Test
    void testWinHorizontally1() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("a2"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("a3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinHorizontally2() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b2"); // 1st player
        sendCommandToController("c2"); // 2nd player
        sendCommandToController("b1"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("b3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinHorizontally3() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinVertically1() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("b1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c1"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinVertically2() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a3"); // 2nd player
        sendCommandToController("c2"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinVertically3() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("c3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinDiagonally1() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a3"); // 2nd player
        sendCommandToController("c3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinDiagonally2() throws OXOMoveException {
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("c1"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    // 2. change win threshold win
    @Test
    void testThresholdWinHorizontally1() throws OXOMoveException {
        controller.addRow(); // 4x3
        controller.addColumn(); // 4x4
        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("a2"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("a3"); // 1st player
        sendCommandToController("b3"); // 2nd player
        sendCommandToController("a4"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinHorizontally2() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        // win threshold: 5
        controller.increaseWinThreshold();
        controller.increaseWinThreshold();

        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b2"); // 1st player
        sendCommandToController("c2"); // 2nd player
        sendCommandToController("b1"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("c3"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("c4"); // 2nd player
        sendCommandToController("b5"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinHorizontally3() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("c1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c3"); // 1st player
        sendCommandToController("b3"); // 2nd player
        sendCommandToController("c4"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinVertically1() throws OXOMoveException {
        controller.addRow(); // 4x3
        controller.addColumn(); // 4x4
        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("b1"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("c1"); // 1st player
        sendCommandToController("c2"); // 2nd player
        sendCommandToController("d1"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinVertically2() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        // win threshold: 5
        controller.increaseWinThreshold();
        controller.increaseWinThreshold();
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("d2"); // 1st player
        sendCommandToController("d1"); // 2nd player
        sendCommandToController("e2"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinVertically3() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("c3"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("d3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinDiagonally1() throws OXOMoveException {
        controller.addRow(); // 4x3
        controller.addColumn(); // 4x4
        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a3"); // 2nd player
        sendCommandToController("c3"); // 1st player
        sendCommandToController("c2"); // 2nd player
        sendCommandToController("d4"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testThresholdWinDiagonally2() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        controller.increaseWinThreshold(); // win threshold: 4
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("d2"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("c3"); // 1st player
        sendCommandToController("b2"); // 2nd player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("a3"); // 2nd player
        sendCommandToController("a5"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    // 3. multiple players win
    @Test
    void testMulPlayersWinHorizontally1() throws OXOMoveException {
        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b1"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("c1"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("c2"); // 3st player
        sendCommandToController("b3"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testMulPlayersWinVertically1() throws OXOMoveException {
        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("a3"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b3"); // 3st player
        sendCommandToController("c2"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testMulPlayersWinDiagonally1() throws OXOMoveException {
        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("a2"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("c2"); // 2nd player
        sendCommandToController("c3"); // 3st player
        sendCommandToController("c1"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    // 4. multiple players and change win threshold win
    @Test
    void testMulThresholdWinHorizontally1() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        // win threshold: 5
        controller.increaseWinThreshold();
        controller.increaseWinThreshold();

        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("b1"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("c1"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("c2"); // 3st player
        sendCommandToController("b3"); // 1st player
        sendCommandToController("a3"); // 2nd player
        sendCommandToController("c3"); // 3st player
        sendCommandToController("b4"); // 1st player
        sendCommandToController("a4"); // 2nd player
        sendCommandToController("c4"); // 3st player
        sendCommandToController("b5"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testMulThresholdWinVertically1() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        controller.increaseWinThreshold(); // win threshold: 4

        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); // 1st player
        sendCommandToController("a1"); // 2nd player
        sendCommandToController("a3"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b3"); // 3st player
        sendCommandToController("c2"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("c3"); // 3st player
        sendCommandToController("d2"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testMulThresholdWinDiagonally1() throws OXOMoveException {
        // 5x5
        controller.addRow();
        controller.addColumn();
        controller.addRow();
        controller.addColumn();

        controller.increaseWinThreshold(); // win threshold: 4

        model.addPlayer(new OXOPlayer('G'));
        // Get 1st player
        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // 1st player
        sendCommandToController("a2"); // 2nd player
        sendCommandToController("a3"); // 3st player
        sendCommandToController("b2"); // 1st player
        sendCommandToController("b1"); // 2nd player
        sendCommandToController("b3"); // 3st player
        sendCommandToController("c3"); // 1st player
        sendCommandToController("c1"); // 2nd player
        sendCommandToController("c2"); // 3st player
        sendCommandToController("d4"); // 1st player

        String failedTestComment = "Winner was expected to be " + firstPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstPlayer, model.getWinner(), failedTestComment);
    }


    //// Below are exception tests
    // 1. Invalid Identifier Length
    @Test
    void testInvalidIdentifierLengthException1() {
        String command = "a";
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(InvalidIdentifierLengthException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }


    @Test
    void testInvalidIdentifierLengthException2() {
        String command = "a1\"";
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(InvalidIdentifierLengthException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }

    @Test
    void testInvalidIdentifierLengthException3() {
        String command = "abc123";
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(InvalidIdentifierLengthException.class, () -> sendCommandToController(command), failMsg);
    }

    @Test
    void testInvalidIdentifierLengthException4() {
        String command = "";
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(InvalidIdentifierLengthException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }

    @Test
    void testInvalidIdentifierLengthException5() {
        String command = "aa1";
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(InvalidIdentifierLengthException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }


    // 2. Invalid Identifier Character
    @Test
    void testInvalidIdentifierCharacterException1() {
        String command = "aB";
        String expected = "B is not a valid character for a COLUMN";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException2() {
        String command = "11";
        String expected = "1 is not a valid character for a ROW";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException3() {
        String command = "1b";
        String expected = "1 is not a valid character for a ROW";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException5() {
        String command = "A]";
        String expected = "] is not a valid character for a COLUMN";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException6() {
        String command = "[2";
        String expected = "[ is not a valid character for a ROW";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException7() {
        String command = " 2";
        String expected = "  is not a valid character for a ROW";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException8() {
        String command = "A ";
        String expected = "  is not a valid character for a COLUMN";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testInvalidIdentifierCharacterException9() {
        String command = "  ";
        String expected = "  is not a valid character for a ROW";
        assertThrows(InvalidIdentifierCharacterException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    // 3. Outside Range
    @Test
    void testOutsideCellRangeExceptionROW1() {
        String command = "d1";
        String expected = "Position 3 is out of range for ROW";
        assertThrows(OutsideCellRangeException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testOutsideCellRangeExceptionROW2() {
        controller.addRow();
        String command = "e1";
        String expected = "Position 4 is out of range for ROW";
        assertThrows(OutsideCellRangeException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testOutsideCellRangeExceptionCOLUMN1() {
        String command = "a4";
        String expected = "Position 3 is out of range for COLUMN";
        assertThrows(OutsideCellRangeException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }

    @Test
    void testOutsideCellRangeExceptionCOLUMN2() {
        controller.addColumn();
        String command = "a5";
        String expected = "Position 4 is out of range for COLUMN";
        assertThrows(OutsideCellRangeException.class, () -> controller.handleIncomingCommand(command));
        try {
            controller.handleIncomingCommand(command);
        } catch (OXOMoveException e) {
            assertEquals(expected, e.getMessage(),expected);
        }
    }


    // 4. Already Taken
    @Test
    void testCellAlreadyTakenException1() throws OXOMoveException {
        String command = "a1";
        controller.handleIncomingCommand(command);
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(CellAlreadyTakenException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }

    @Test
    void testCellAlreadyTakenException2() throws OXOMoveException {
        String command = "a1";
        controller.handleIncomingCommand(command);
        String command_2 = "a2";
        controller.handleIncomingCommand(command_2);
        String failMsg = "Invalid identifier length for command: " + command;
        assertThrows(CellAlreadyTakenException.class, () -> controller.handleIncomingCommand(command), failMsg);
    }

    //// Below are add/remove column tests
    @Test
    void testAddColumnNoWinner() {
        // Add a column
        controller.addColumn();

        // Check column number
        String failMsg = "Number of columns should be 4";
        assertEquals(4, model.getNumberOfColumns(),failMsg);

    }

    @Test
    public void testAddColumnHasWinner() {
        // Set a winner
        model.setWinner(model.getPlayerByNumber(0));

        // Add a column
        controller.addColumn();

        // Check column number
        String failMsg = "Number of columns should be 4";
        assertEquals(4, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testAddColumnMax() {
        for (int i = 0; i < 6; i++) {
            controller.addColumn();
        }
        // Add a column
        controller.addColumn();

        // Check column number
        String failMsg = "Number of columns should be 9";
        assertEquals(9, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testRemoveColumnMin() {
        // Try to remove a column
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();

        // Check column number
        String failMsg = "Number of columns should be 1";
        assertEquals(1, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testRemoveColumn1() {
        // Add a column
        controller.addColumn();

        // Try to remove a column
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();

        // Check column number
        String failMsg = "Number of columns should be 1";
        assertEquals(1, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testRemoveColumn2() {
        // Add 2 columns
        controller.addColumn();
        controller.addColumn();

        // Try to remove a column
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();
        controller.removeColumn();

        // Check column number
        String failMsg = "Number of columns should be 1";
        assertEquals(1, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testRemoveColumn3() {
        // Add to 9 columns
        for (int i = 0; i < 9; i++) {
            controller.addColumn();
        }

        // Try to remove a column
        controller.removeColumn();

        // Check column number
        String failMsg = "Number of columns should be 8";
        assertEquals(8, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testAddColumnWithDraw() {
        // Column can be added when game is drawn

        model.setGameDrawn();
        // Try to add a column
        controller.addColumn();
        // Check column number
        String failMsg = "Number of columns should be 4";
        assertEquals(4, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testAddColumnAfterReset() {
        controller.reset();
        // Try to add a column
        controller.addColumn();
        // Check column number
        String failMsg = "Number of columns should be 4";
        assertEquals(4, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testRemoveColumnAfterReset() {
        controller.reset();
        // Try to remove a column
        controller.removeColumn();
        // Check column numbers
        String failMsg = "Number of columns should be 2";
        assertEquals(2, model.getNumberOfColumns(),failMsg);
    }

    @Test
    public void testAddAndRemoveColumn() {
        // Try to remove a column
        controller.removeColumn();
        // Check column number
        String failMsg = "Number of columns should be 2";
        assertEquals(2, model.getNumberOfColumns(),failMsg);

        // Try to add a column
        controller.addColumn();
        // Check column number
        failMsg = "Number of columns should be 3";
        assertEquals(3, model.getNumberOfColumns(),failMsg);

        // Try to remove a column
        controller.removeColumn();
        // Check column number
        failMsg = "Number of columns should be 2";
        assertEquals(2, model.getNumberOfColumns(),failMsg);

        // Try to remove a column
        controller.removeColumn();
        // Check column number
        failMsg = "Number of columns should be 1";
        assertEquals(1, model.getNumberOfColumns(),failMsg);
    }


    //// Below are add/remove row tests
    @Test
    void testAddRowNoWinner() {
        // Add a row
        controller.addRow();

        // Check that a row
        String failMsg = "Number of rows should be 4";
        assertEquals(4, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testAddRowHasWinner() {
        // Set a winner
        model.setWinner(model.getPlayerByNumber(0));

        // Add a row
        controller.addRow();

        // Check row numbers
        String failMsg = "Number of rows should be 4";
        assertEquals(4, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testAddRowMax() {
        for (int i = 0; i < 6; i++) {
            controller.addRow();
        }
        // Try to add a row
        controller.addRow();

        // Check row numbers
        String failMsg = "Number of rows should be 9";
        assertEquals(9, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testRemoveRowMin() {
        // Try to remove a row
        controller.removeRow();
        controller.removeRow();
        controller.removeRow();
        controller.removeRow();

        // Check row numbers
        String failMsg = "Number of rows should be 1";
        assertEquals(1, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testRemoveRow1() {
        // Add a row
        controller.addRow();

        // Try to remove a row
        controller.removeRow();

        // Check row numbers
        String failMsg = "Number of rows should be 3";
        assertEquals(3, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testRemoveRow2() {
        // Add 2 rows
        controller.addRow();
        controller.addRow();

        // Try to remove a row
        controller.removeRow();

        // Check row numbers
        String failMsg = "Number of rows should be 4";
        assertEquals(4, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testRemoveRow3() {
        // Add to 9 rows
        for (int i = 0; i < 9; i++) {
            controller.addRow();
        }

        // Try to remove a row
        controller.removeRow();

        // Check row numbers
        String failMsg = "Number of rows should be 8";
        assertEquals(8, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testAddRowWithDraw() {
        // Row can be added when game is drawn

        model.setGameDrawn();
        // Try to add a row
        controller.addRow();
        // Check row numbers
        String failMsg = "Number of rows should be 4";
        assertEquals(4, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testAddRowAfterReset() {
        controller.reset();
        // Try to add a row
        controller.addRow();
        // Check row numbers
        String failMsg = "Number of rows should be 4";
        assertEquals(4, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testRemoveRowAfterReset() {
        controller.reset();
        // Try to remove a row
        controller.removeRow();
        // Check row numbers
        String failMsg = "Number of rows should be 2";
        assertEquals(2, model.getNumberOfRows(),failMsg);
    }

    @Test
    public void testAddAndRemoveRow() {
        // Try to remove a row
        controller.removeRow();
        // Check row numbers
        String failMsg = "Number of rows should be 2";
        assertEquals(2, model.getNumberOfRows(),failMsg);

        // Try to add a row
        controller.addRow();
        // Check row numbers
        failMsg = "Number of rows should be 3";
        assertEquals(3, model.getNumberOfRows(),failMsg);

        // Try to remove a row
        controller.removeRow();
        // Check row numbers
        failMsg = "Number of rows should be 2";
        assertEquals(2, model.getNumberOfRows(),failMsg);

        // Try to remove a row
        controller.removeRow();
        // Check row numbers
        failMsg = "Number of rows should be 1";
        assertEquals(1, model.getNumberOfRows(),failMsg);
    }
}
