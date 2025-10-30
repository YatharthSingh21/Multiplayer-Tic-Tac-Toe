package com.tictac.server.model;

public class Game {
    private boolean p1Turn = true;
    private Player p1;
    private Player p2;
    private Board b;

    public Game(Player p1, Player p2, Board b) {
        this.p1 = p1;
        this.p2 = p2;
        this.b = b;
    }

    //getters , setters are not needed
    public Player getP1() {return this.p1;}
    public void setP1(Player p1) { this.p1 = p1; }
    public Player getP2() {return this.p2;}
    public void setP2(Player p2) { this.p2 = p2; }
    public Board getBoard() {return b;}


    public GameResult applyMove(Player p, int x, int y) {
        int size = b.getSize();

        if (x < 0 || y < 0 || x >= size || y >= size || b.getCell(x, y) != '_') {
            return GameResult.INVALID;
        }

        b.placeMove(x, y, p.symbol);

        if (p.checkWin(x, y, size)) {
            return GameResult.WIN;
        }

        if(b.isFull()){
            return GameResult.DRAW;
        }

        // toggle turn
        p1Turn = !p1Turn;
        return GameResult.NEXT;
    }

    public Player getCurrentPlayer() {
        return p1Turn ? p1 : p2;
    }

    @Override
    public String toString() {
        return b.boardToString();
    }
}
