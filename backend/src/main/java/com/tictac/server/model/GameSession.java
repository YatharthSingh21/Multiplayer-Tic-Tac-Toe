package com.tictac.server.model;

import java.util.UUID;

public class GameSession {
    private String gameId;
    private Game game;
    private String player1Id;
    private String player2Id;

    public GameSession(){}

    public GameSession(Game game, String p1Id, String p2Id) {
        this.gameId = UUID.randomUUID().toString();
        this.game = game;
        this.player1Id = p1Id;
        this.player2Id = p2Id;
    }

    public String getGameId() { return gameId; }
    public Game getGame() { return game; }
    public String getPlayer1Id() { return player1Id; }
    public String getPlayer2Id() { return player2Id; }
}
