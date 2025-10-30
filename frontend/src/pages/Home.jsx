import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router"; 
import "./Home.css";

function Home() {
  const [username, setUsername] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem("username");
    if (!storedUser) {
      navigate("/login");
    } else {
      setUsername(storedUser);
    }
  }, [navigate]);

  const handleCreateGame = async () => {
    if (!username) {
      alert("Please login first!");
      navigate("/login");
      return;
    }

    navigate("/lobby", { state: { playerId: username } });
  };

  const handleLogout = () => {
    localStorage.removeItem("username");
    navigate("/login");
  };

  return (
    <div className="home-container">
      <div className="heading-section">
        <h1>Welcome, {username || "Player"}!</h1>
        <div className="xo-decorations">
          <span className="xo-decoration">⭕</span>
          <span className="xo-decoration">❌</span>
          <span className="xo-decoration">⭕</span>
        </div>
      </div>

      <div className="form-section">
        <button onClick={handleCreateGame} className="home-btn primary-btn">
          🎮 New Game
        </button>

        <Link to="/leaderboard" className="home-btn leaderboard-link">
          🏆 View Leaderboard
        </Link>

        <button onClick={handleLogout} className="home-btn logout-btn">
          🚪 Logout
        </button>
      </div>
    </div>
  );
}

export default Home;