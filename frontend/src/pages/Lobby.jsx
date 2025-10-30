import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import axios from "axios";
import { Client } from "@stomp/stompjs";
import { WS_BASE_URL, API_BASE } from "../config/config";

const Lobby = () => { 
  const [status, setStatus] = useState("Joining lobby...");
  const [username, setUsername] = useState(null);
  const navigate = useNavigate();

 useEffect(() => {
  const storedUsername = localStorage.getItem("username");
  
  if (!storedUsername) {
    navigate("/login");
    return;
  }
  
  setUsername(storedUsername);
  
  let stompClient;
  let hasJoined = false; // for preventing multiple joins

  const connectWebSocket = () => {
    stompClient = new Client({
      brokerURL: `${WS_BASE_URL}/ws`,
      reconnectDelay: 5000,

      onConnect: () => {
        console.log("✅ Connected to WebSocket lobby");

        stompClient.subscribe("/topic/lobby", (message) => {
          const update = JSON.parse(message.body);
          console.log("💡 Received lobby update:", update);

          if (update.status === "matched") {
            const isPlayer1 = update.player1 === storedUsername;
            const isPlayer2 = update.player2 === storedUsername;
            
            if (isPlayer1 || isPlayer2) {
              console.log("I am part of this match!");
              setStatus(`Matched! Game ID: ${update.gameId}`);
              
              if (stompClient) {
                stompClient.deactivate();
              }
              
              setTimeout(() => {
                navigate(`/game/${update.gameId}/${storedUsername}`, {
                  state: { player1: update.player1, player2: update.player2 },
                });
              }, 1000);
            } else {
              console.log("ℹ️ Other players matched, not me");
            }
          }
        });
      },

      onStompError: (frame) => {
        console.error("Broker error:", frame);
      },
    });

    stompClient.activate();
  };

  const joinLobby = async () => {
    //Prevent duplicate calls
    if (hasJoined) {
      console.log("⚠️ Already joined, skipping");
      return;
    }
    
    hasJoined = true;
    
    try {
      console.log("📡 Sending join request for:", storedUsername);
      
      const response = await axios.post(`${API_BASE}/api/matchmaking/join`, {
        username: storedUsername,
      });

      const data = response.data;
      console.log("📥 Matchmaking response:", data);

      if (data.status === "waiting") {
        setStatus("Waiting for an opponent...");
      } else if (data.status === "matched") {
        setStatus(`Matched! Game ID: ${data.gameId}`);
        
        if (stompClient) {
          stompClient.deactivate();
        }
        
        setTimeout(() => {
          navigate(`/game/${data.gameId}/${storedUsername}`);
        }, 1000);
      } else if (data.status === "in_game") {
        console.log("⚠️ Already in a game");
        setStatus("You are already in a game");
      }
    } catch (error) {
      console.error("Error joining lobby:", error);
      setStatus("Failed to connect to lobby 😔");
    }
  };

  connectWebSocket();
  joinLobby();

  return () => {
    if (stompClient) {
      stompClient.deactivate();
    }
  };
}, [navigate]); 

  if (!username) {
    return (
      <div style={{ textAlign: "center", marginTop: "100px" }}>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div style={{ textAlign: "center", marginTop: "100px" }}>
      <h1>Tic Tac Toe Lobby</h1>
      <p><strong>Username:</strong> {username}</p>  
      <p><strong>Status:</strong> {status}</p>
    </div>
  );
};

export default Lobby;