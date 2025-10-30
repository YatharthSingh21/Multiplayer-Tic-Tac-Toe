package com.tictac.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tictac.server.model.Board;

public class GameSessionResponse {

    @JsonProperty("gameId")
    private String gameId;
    private String player1;
    private String player2;
    private char[][] board; // or String[] if easier

    // constructor, getters, setters
    public GameSessionResponse(String gameId, String player1, String player2, Board b){
        setGameId(gameId);
        setPlayer1(player1);
        setPlayer2(player2);
        this.board = b.getGrid();
    }

    public String getGameId(){return this.gameId;}
    public void setGameId(String gameId) {this.gameId = gameId;}

    public void setPlayer1(String player1) {this.player1 = player1;}
    public String getPlayer1(){return this.player1;}

    public void setPlayer2(String player2) {this.player2 = player2;}
    public String getPlayer2() {return player2;}

    public char[][] getBoard() { return board;}
    public void setBoard(char[][] board) { this.board = board;}
}
