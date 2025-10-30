package com.tictac.server.controller;

import com.tictac.server.model.GameSession;
import com.tictac.server.service.MatchmakingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/matchmaking")
@CrossOrigin(origins = {"http://localhost:5173", "https://your-frontend-url.onrender.com"})
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/join")
    public Map<String, Object> joinLobby(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        return matchmakingService.joinLobby(username);
    }

    @GetMapping("/game/{gameId}/state")
    public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String gameId) {
        System.out.println("🔍 Fetching game state for: " + gameId);

        GameSession session = matchmakingService.getGame(gameId);

        if (session == null) {
            System.out.println("ame session not found");
            return ResponseEntity.notFound().build();
        }

        try {
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}