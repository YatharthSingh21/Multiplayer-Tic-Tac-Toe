import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { connectWebSocket, sendMove, disconnectWebSocket } from "../services/socket.js";
import "./GameBoard.css";

const GameBoard = ({ gameId, playerId, player1, player2 }) => {
  const [board, setBoard] = useState("---------");
  const [turn, setTurn] = useState(null);
  const [result, setResult] = useState(null);
  const [gameOver, setGameOver] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!gameId || !playerId) return;

    console.log("Connecting WebSocket for game:", gameId, "player:", playerId);

    const fetchInitialState = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/matchmaking/game/${gameId}/state`);
        if (response.ok) {
          const state = await response.json();
          console.log("📥 Fetched initial game state:", state);
          
          setBoard(state.board || "---------");
          setTurn(state.nextPlayerId || state.nextTurnPlayer || null);
          setResult(state.result || "NEXT");
        }
      } catch (error) {
        console.error("Error fetching initial game state:", error);
      }
    };

    fetchInitialState();

    connectWebSocket(gameId, playerId, (update) => {
      console.log("CALLBACK FIRED - Received update from server:", update);

      if (!update || update.gameId !== gameId) {
        console.warn("Update mismatch or null");
        return;
      }

      setBoard(update.board || update.boardState || "---------");
      setTurn(update.nextTurnPlayer || update.nextPlayerId || null);
      setResult(update.status || update.result || null);

      if (update.result === "WIN" || update.result === "DRAW" || 
          update.status === "WIN" || update.status === "DRAW") {
        setGameOver(true);
      }
    });

    // ✅ Cleanup: Disconnect WebSocket when leaving game
    return () => {
      console.log("Game ended -- disconnecting WebSocket");
      disconnectWebSocket();
    };
  }, [gameId, playerId]);

  useEffect(() => {
    if (gameOver && result) {
      setTimeout(() => {
        let message = "";
        
        if (result === "WIN") {

          const isWinner = turn !== playerId; // If it's not your turn after WIN, you won
          message = isWinner 
            ? "🎉 Congratulations! You Won! 🏆" 
            : "😔 You Lost. Better luck next time!";
        } else if (result === "DRAW") {
          message = "🤝 It's a Draw!";
        }
        
        const goToLobby = window.confirm(message + "\n\nLet's go back home");
        if (goToLobby) {
          navigate("/home");
        }
      }, 500);
    }
  }, [gameOver, result, turn, playerId, navigate]);

  const handleMove = (x, y) => {
    if (gameOver) {
      console.warn("Game is over, no more moves allowed");
      return;
    }

    if (turn && turn !== playerId) {
      console.warn("Not your turn!");
      alert("⚠️ It's not your turn! Wait for your opponent.");
      return;
    }

    console.log("Sending move:", { gameId, playerId, x, y });

    if (!gameId || !playerId) {
      console.warn("Missing gameId or playerId, cannot send move.");
      return;
    }

    sendMove({ gameId, playerId, x, y });
  };

  const boardArray = (board && typeof board === 'string') ? board.split("") : Array(9).fill("-");

  const getTurnDisplay = () => {
    if (!turn) return "Waiting...";
    if (turn === playerId) return "Your Turn! 🎯";
    return `${turn}'s Turn`;
  };

  const getResultDisplay = () => {
    if (!result || result === "NEXT") return "In Progress 🎮";
    if (result === "WIN") {
      return turn === playerId ? "You Lost 😔" : "You Won! 🏆";
    }
    if (result === "DRAW") return "Draw 🤝";
    return result;
  };

  return (
    <div className="gameboard-container">
      <div className="gameboard-header">
        <h1>Tic Tac Toe</h1>
        <div className="xo-decorations">
          <span>⭕</span>
          <span>❌</span>
          <span>⭕</span>
        </div>
      </div>

      <div className="game-info">
        <p><strong>Status:</strong> <span style={{ 
          color: gameOver ? "#e74c3c" : "#2ecc71",
          fontWeight: "bold"
        }}>{getResultDisplay()}</span></p>
        
        <p><strong>Turn:</strong> <span style={{
          color: turn === playerId ? "#3498db" : "#95a5a6",
          fontWeight: "bold"
        }}>{getTurnDisplay()}</span></p>
        
        <p><strong>You:</strong> {playerId}</p>
        
        {player1 && player2 && (
          <p style={{ fontSize: "0.9rem", color: "#666" }}>
            <strong>Match:</strong> {player1} vs {player2}
          </p>
        )}
      </div>

      <div className="board-grid">
        {boardArray.map((cell, i) => {
          const isEmpty = cell === "-" || cell === "";
          const isYourTurn = turn === playerId;
          
          return (
            <button
              key={i}
              className={`grid-cell ${isEmpty && isYourTurn && !gameOver ? "hoverable" : ""}`}
              onClick={() => handleMove(Math.floor(i / 3), i % 3)}
              disabled={gameOver || !isEmpty || turn !== playerId}
              style={{
                cursor: (gameOver || !isEmpty || turn !== playerId) ? "not-allowed" : "pointer",
                opacity: (gameOver || turn !== playerId) ? 0.7 : 1
              }}
            >
              {cell !== "-" ? (
                <span style={{ 
                  fontSize: "2rem", 
                  color: cell === "X" ? "#e74c3c" : "#3498db" 
                }}>
                  {cell}
                </span>
              ) : ""}
            </button>
          );
        })}
      </div>

      {gameOver && (
        <div style={{ marginTop: "20px" }}>
          <button
            onClick={() => navigate("/home")}
            style={{
              padding: "10px 30px",
              fontSize: "1rem",
              backgroundColor: "#3498db",
              color: "white",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer"
            }}
          >
            Return to Home
          </button>
        </div>
      )}
    </div>
  );
};

export default GameBoard;