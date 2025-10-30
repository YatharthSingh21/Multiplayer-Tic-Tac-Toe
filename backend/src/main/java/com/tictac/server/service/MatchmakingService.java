package com.tictac.server.service;

import com.tictac.server.dto.GameUpdate;
import com.tictac.server.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MatchmakingService {

    private final Queue<String> waitingPlayers = new ConcurrentLinkedQueue<>();
    private final Map<String, GameSession> activeGames = new HashMap<>();
    private final Set<String> playersInGame = new HashSet<>(); // ✅ Track players in games

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public synchronized Map<String, Object> joinLobby(String username) {
        System.out.println("\n🔵 ========== JOIN LOBBY REQUEST ==========");
        System.out.println("🔵 Player: " + username);
        System.out.println("🔵 Queue before: " + waitingPlayers);
        System.out.println("🔵 Active games: " + activeGames.size());
        System.out.println("🔵 Players in games: " + playersInGame);

        Map<String, Object> response = new HashMap<>();

        // ✅ Check if player is already in an active game
        if (playersInGame.contains(username)) {
            System.out.println("⚠️ Player " + username + " is already in an active game!");
            response.put("status", "in_game");
            response.put("message", "You are already in a game");
            System.out.println("🔵 ==========================================\n");
            return response;
        }

        // ✅ Check if already in queue
        if (waitingPlayers.contains(username)) {
            System.out.println("⚠️ Player " + username + " is already in the queue!");
            response.put("status", "waiting");
            System.out.println("🔵 ==========================================\n");
            return response;
        }

        if (waitingPlayers.isEmpty()) {
            waitingPlayers.add(username);
            response.put("status", "waiting");
            System.out.println("✅ Added " + username + " to empty queue");
            System.out.println("🔵 Queue after: " + waitingPlayers);
            System.out.println("🔵 ==========================================\n");
        } else {
            String opponent = waitingPlayers.poll();
            System.out.println("✅ Removed opponent from queue: " + opponent);
            System.out.println("🔵 Queue after poll: " + waitingPlayers);

            Board b = new Board(3);
            Player p1 = new HumanPlayer(opponent, 'O', b.getSize());
            Player p2 = new HumanPlayer(username, 'X', b.getSize());
            Game game = new Game(p1, p2, b);

            GameSession session = new GameSession(game, opponent, username);
            activeGames.put(session.getGameId(), session);

            // ✅ Mark both players as in a game
            playersInGame.add(opponent);
            playersInGame.add(username);

            response.put("status", "matched");
            response.put("gameId", session.getGameId());
            response.put("player1", opponent);
            response.put("player2", username);

            System.out.println("🎮 MATCH CREATED:");
            System.out.println("   GameId: " + session.getGameId());
            System.out.println("   Player1: " + opponent);
            System.out.println("   Player2: " + username);

            String initialBoard = game.getBoard().boardToString();
            String firstPlayer = opponent;

            GameUpdate initialGameState = new GameUpdate(
                    session.getGameId(),
                    "NEXT",
                    initialBoard,
                    firstPlayer
            );

            messagingTemplate.convertAndSend("/topic/game/" + session.getGameId(), initialGameState);
            System.out.println("📤 Sent initial game state to /topic/game/" + session.getGameId());

            Map<String, Object> matchUpdate = new HashMap<>();
            matchUpdate.put("status", "matched");
            matchUpdate.put("gameId", session.getGameId());
            matchUpdate.put("player1", opponent);
            matchUpdate.put("player2", username);

            messagingTemplate.convertAndSend("/topic/lobby", matchUpdate);
            System.out.println("📤 Sent lobby notification");
            System.out.println("🔵 ==========================================\n");
        }

        return response;
    }

    public void removeGame(String gameId) {
        GameSession session = activeGames.remove(gameId);

        // ✅ Remove players from the in-game set
        if (session != null) {
            playersInGame.remove(session.getPlayer1Id());
            playersInGame.remove(session.getPlayer2Id());
            System.out.println("🧹 Removed players from game: " + session.getPlayer1Id() + ", " + session.getPlayer2Id());
        }
    }

    public GameSession getGame(String gameId) {
        return activeGames.get(gameId);
    }
}