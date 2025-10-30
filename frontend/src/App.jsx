import { BrowserRouter, Routes, Route } from "react-router";

import Home from "./pages/Home.jsx";
import LeaderBoard from "./pages/LeaderBoard.jsx";
import Game from "./pages/Game.jsx";
import Login from "./pages/Login.jsx";
import Lobby from "./pages/Lobby.jsx";

import "./App.css";

const getPlayerId = () => {
  let id = localStorage.getItem("playerId");
  if (!id) {
    id = "player-" + Math.floor(Math.random() * 10000);
    localStorage.setItem("playerId", id);
  }
  return id;
};

function App() {
  const playerId = getPlayerId();

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/home" element={<Home />} />
        <Route path="/lobby" element={<Lobby />} />
        <Route path="/login" element={<Login />} />
        <Route path="/leaderboard" element={<LeaderBoard />} />
        <Route path="/game/:gameId/:playerId" element={<Game />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
