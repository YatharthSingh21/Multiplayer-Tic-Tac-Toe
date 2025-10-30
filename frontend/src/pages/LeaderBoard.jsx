import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router";
import "./Leaderboard.css";

function Leaderboard() {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const username = localStorage.getItem("username");
    if (!username) {
      navigate("/login");
      return;
    }

    const fetchLeaderboard = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/leaderboard");
        if (!res.ok) throw new Error("Failed to fetch leaderboard data");
        const data = await res.json();
        setPlayers(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchLeaderboard();
  }, [navigate]);

  if (loading) return <div className="leaderboard-container"><p className="loading">Loading leaderboard...</p></div>;
  if (error) return <div className="leaderboard-container"><p className="error">{error}</p></div>;

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-header">
        <h1>🏆 Leaderboard</h1>
        <div className="xo-decorations">
          <span>⭕</span>
          <span>❌</span>
          <span>⭕</span>
        </div>
      </div>

      <div className="leaderboard-content">
        <table className="leaderboard-table">
          <thead>
            <tr>
              <th>Rank</th>
              <th>Player</th>
              <th>Wins</th>
              <th>Losses</th>
              <th>Draws</th>
            </tr>
          </thead>
          <tbody>
            {players.length === 0 ? (
              <tr>
                <td colSpan="5">No players found</td>
              </tr>
            ) : (
              players.map((player, index) => (
                <tr key={player.id}>
                  <td>{index + 1}</td>
                  <td>{player.username}</td>
                  <td>{player.wins}</td>
                  <td>{player.losses}</td>
                  <td>{player.draws}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        <Link to="/home" className="back-btn">← Back to Home</Link>
      </div>
    </div>
  );
}

export default Leaderboard;