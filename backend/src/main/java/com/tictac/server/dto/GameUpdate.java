package com.tictac.server.dto;

public class GameUpdate {
    private String gameId;
    private String status; // "NEXT", "WIN", "DRAW", "INVALID"
    private String board;  // serialized board
    private String nextTurnPlayer;

    public GameUpdate(String gameId, String status, String board, String nextTurnPlayer) {
        this.gameId = gameId;
        this.status = status;
        this.board = board;
        this.nextTurnPlayer = nextTurnPlayer;
    }

    public String getGameId() { return gameId; }
    public String getStatus() { return status; }
    public String getBoard() { return board; }
    public String getNextTurnPlayer() { return nextTurnPlayer; }
}
