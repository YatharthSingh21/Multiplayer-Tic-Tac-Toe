import { useParams, useLocation } from "react-router";
import GameBoard from "./GameBoard.jsx";

const Game = () => {
  const { gameId, playerId } = useParams();
  const location = useLocation();
  const player1 = location.state?.player1;
  const player2 = location.state?.player2;

  if (!gameId || !playerId) {
    console.warn("Missing gameId or playerId in Game.jsx");
    return <p>⚠️ Missing game details. Please return to the lobby.</p>;
  }

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <GameBoard
        gameId={gameId}
        playerId={playerId}
        player1={player1}
        player2={player2}
      />
    </div>
  );
};

export default Game;
