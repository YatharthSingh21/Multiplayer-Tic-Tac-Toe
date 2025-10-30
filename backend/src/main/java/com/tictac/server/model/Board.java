package com.tictac.server.model;

public class Board {
    private final int size;
    private final char[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        // initialize empty cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '_'; // '_' represents empty
            }
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    // Get the value at a cell
    public char getCell(int row, int col) {
        if (row < 0 || col < 0 || row >= size || col >= size) return '\0';
        return grid[row][col];
    }

    // Place a move on the board
    // Returns true if successful, false if cell is already occupied
    public boolean placeMove(int row, int col, char symbol) {
        if (grid[row][col] == '_') {
            grid[row][col] = symbol;
            return true;
        }
        return false;
    }

    // Check if the board is full (for draw)
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == '_') return false;
            }
        }
        return true;
    }

    // Optional: get the current board as a string for sending to frontend
    public String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char cell=grid[i][j];
                sb.append(cell == '\0' || cell == ' ' || cell == '_' ? '-' : cell);
            }
        }
        return sb.toString();
    }

    public int getSize() {
        return size;
    }
}
