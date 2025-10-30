package com.tictac.server.model;

public abstract class Player {
    protected String name;
    protected char symbol;
    protected int[] rowCnt;
    protected int[] colCnt;
    protected int diag;
    protected int antiDiag;

    public Player(String n, char s, int size) {
        this.name = n;
        this.symbol = s;
        this.rowCnt = new int[size];
        this.colCnt = new int[size];
        this.diag = 0;
        this.antiDiag = 0;
    }

    public abstract void setName(String name);
    public abstract String getName();
    public abstract void setSymbol(String name);
    public abstract char getSymbol();

    public abstract int[] getMove(Board b);
    public abstract boolean checkWin(int r, int c, int n);
}
