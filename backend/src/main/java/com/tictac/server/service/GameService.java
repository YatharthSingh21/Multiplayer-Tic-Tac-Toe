package com.tictac.server.service;

import com.tictac.server.model.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameSession> activeGames = new ConcurrentHashMap<>();

    // 1️⃣ Create new game between two players
    public GameSession createGame(String player1Id, String player2Id) {
        try {
            Board board = new Board(3);
            Player p1 = new HumanPlayer(player1Id, 'X', 3);
            Player p2 = new HumanPlayer(player2Id, 'O', 3);
            Game game = new Game(p1, p2, board);
            GameSession session = new GameSession(game, player1Id, player2Id);
            activeGames.put(session.getGameId(), session);
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // rethrow so Spring still knows there was an error
        }
    }

    // 2️⃣ Get game by ID
    public GameSession getGame(String gameId) {
        return activeGames.get(gameId);
    }

    // 3️⃣ Process player move
    public String processMove(GameSession session, String gameId, String playerId, int x, int y) {
        //GameSession session = activeGames.get(gameId);

        if (session == null) {
            System.out.println("❌ Game not found: " + gameId);
            return null;  // ✅ Return null instead of string
        }

        Game game = session.getGame();
        Player current;
        if (session.getPlayer1Id().equals(playerId)) {
            current = game.getP1();
        } else if (session.getPlayer2Id().equals(playerId)) {
            current = game.getP2();
        } else {
            System.out.println("❌ Player " + playerId + " is not in this game");
            return null;  // Player not in game
        }

        if (current instanceof HumanPlayer) {
            if (!game.getCurrentPlayer().equals(current)) {
                System.out.println("❌ Not " + playerId + "'s turn");
                return null;  // ✅ Return null for invalid turn
            }
            ((HumanPlayer) current).setMove(x, y);
        }

        GameResult result = game.applyMove(current, x, y);

        if (result == GameResult.WIN) {
            System.out.println("🏆 " + playerId + " wins!");
            removeGame(gameId);
            return "WIN";
        } else if (result == GameResult.NEXT) {
            System.out.println("✅ Move accepted, next player's turn");
            return "NEXT";
        } else if (result == GameResult.DRAW) {
            System.out.println("🤝 Game ended in draw");
            removeGame(gameId);
            return "DRAW";
        } else {
            System.out.println("❌ Invalid move at (" + x + ", " + y + ")");
            return null;  // ✅ Return null for invalid moves
        }
    }

    // 4️⃣ Remove finished game
    public void removeGame(String gameId) {
        activeGames.remove(gameId);
    }

    // Optional: expose active game count
    public int getActiveGameCount() {
        return activeGames.size();
    }
}
