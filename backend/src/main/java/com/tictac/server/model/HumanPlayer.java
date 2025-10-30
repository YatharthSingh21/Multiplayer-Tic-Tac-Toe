package com.tictac.server.model;

public class HumanPlayer extends Player {
    private int[] lastMove;

    public HumanPlayer(String name, char symbol, int size) {
        super(name, symbol, size);
    }

    // called when the frontend sends the move
    public void setMove(int x, int y) {
        this.lastMove = new int[]{x, y};
    }

    @Override
    public int[] getMove(Board b) {
        return lastMove;
    }

    @Override
    public void setName(String name) {
        this.name = name; // assign to inherited field
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setSymbol(String symbol) {
        if (symbol != null && symbol.length() == 1) {
            this.symbol = symbol.charAt(0); // convert string to char
        }
    }

    @Override
    public char getSymbol() {
        return this.symbol;
    }

    @Override
    public boolean checkWin(int x, int y, int n){
        rowCnt[x]++;
        colCnt[y]++;
        if(x==y) diag++;
        if(x+y==n-1) antiDiag++;

        return (rowCnt[x]==n || colCnt[y]==n || diag==n || antiDiag==n);
    }

    @Override
    public String toString() {
        return "Player{name=" + name + ", symbol=" + symbol + "}";
    }
}