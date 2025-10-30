package com.tictac.server.controller;

import com.tictac.server.dto.*;
import com.tictac.server.model.GameSession;
import com.tictac.server.service.GameService;
import com.tictac.server.service.MatchmakingService;
import com.tictac.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GameSocketController {

    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/move")
    public void handleMove(MoveMessage move) {

        System.out.println("The controller was hit successfully");

        if (move == null || move.getGameId() == null) {
            System.out.println("Invalid move message: missing gameId");
            return;
        }

        // ✅ Use matchmakingService instead of gameService
        GameSession session = matchmakingService.getGame(move.getGameId());
        if (session == null){
            System.out.println("Session is null for gameId: " + move.getGameId());
            return;
        }

        // Still use gameService for move processing logic
        String result = gameService.processMove(session, move.getGameId(), move.getPlayerId(), move.getX(), move.getY());
        if (result == null) {
            System.out.println("Gameservice returns null.");
            return;
        }

        String boardState = session.getGame().getBoard().boardToString();

        // ✅ NEW: Update leaderboard when game ends
        if (result.equals("WIN") || result.equals("DRAW")) {
            String player1 = session.getPlayer1Id();
            String player2 = session.getPlayer2Id();
            String currentPlayer = move.getPlayerId();

            if (result.equals("WIN")) {
                // Current player wins
                userService.updateStats(currentPlayer, "WIN");
                // Opponent loses
                String opponent = player1.equals(currentPlayer) ? player2 : player1;
                userService.updateStats(opponent, "LOSS");

                System.out.println("🏆 " + currentPlayer + " wins! " + opponent + " loses.");
            } else if (result.equals("DRAW")) {
                // Both players draw
                userService.updateStats(player1, "DRAW");
                userService.updateStats(player2, "DRAW");

                System.out.println("🤝 Draw between " + player1 + " and " + player2);
            }

            matchmakingService.removeGame(move.getGameId());
            System.out.println("Clean up game session: " + move.getGameId());
        }

        GameUpdate update = new GameUpdate(
                move.getGameId(),
                result,
                boardState,
                result.equals("NEXT")
                        ? (session.getPlayer1Id().equals(move.getPlayerId())
                        ? session.getPlayer2Id() : session.getPlayer1Id())
                        : null
        );

        System.out.println("Received move: " + move);
        System.out.println("Broadcasting to: /topic/game/" + move.getGameId());

        messagingTemplate.convertAndSend("/topic/game/" + move.getGameId(), update);
    }

    @GetMapping("/game/{gameId}/state")
    public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String gameId) {
        GameSession session = matchmakingService.getGame(gameId);

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        String board = session.getGame().getBoard().boardToString();
        String currentPlayer = session.getGame().getCurrentPlayer().getName();

        Map<String, Object> state = Map.of(
                "gameId", gameId,
                "board", board,
                "nextPlayerId", currentPlayer,
                "result", "NEXT",
                "player1", session.getPlayer1Id(),
                "player2", session.getPlayer2Id()
        );

        return ResponseEntity.ok(state);
    }
}