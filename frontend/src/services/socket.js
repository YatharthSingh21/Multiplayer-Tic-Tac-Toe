import { Client } from "@stomp/stompjs";
import { WS_BASE_URL } from "../config/config";

let stompClient = null;
let currentGameId = null;

export const connectWebSocket = (gameId, playerId, onMessage) => {
  // ✅ Only skip if already connected to THIS SPECIFIC game
  if (stompClient && stompClient.connected && currentGameId === gameId) {
    console.log("⚠️ Already connected to this game");
    return;
  }

  // ✅ Disconnect from previous game if switching games
  if (stompClient && currentGameId !== gameId) {
    console.log("🔄 Switching games, disconnecting from previous game");
    stompClient.deactivate();
    stompClient = null;
  }

  currentGameId = gameId;

  stompClient = new Client({
    brokerURL: `${WS_BASE_URL}/ws`,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    
    onConnect: () => {
      console.log("✅ Connected to WebSocket for game:", gameId, "player:", playerId);

      // ✅ Subscribe to this specific game
      stompClient.subscribe(`/topic/game/${gameId}`, (message) => {
        console.log("📥 RAW message received:", message.body);
        const update = JSON.parse(message.body);
        console.log("📥 Parsed update:", update);
        onMessage(update);
      });
      
      console.log("✅ Subscribed to /topic/game/" + gameId);
    },
    
    onStompError: (frame) => {
      console.error("❌ STOMP Error:", frame);
    },
    
    onWebSocketClose: () => {
      console.log("🔌 WebSocket closed");
    },
    
    onWebSocketError: (error) => {
      console.error("❌ WebSocket Error:", error);
    },
    
    debug: (str) => {
      console.log("[STOMP Debug]", str);
    }
  });

  stompClient.activate();
};

export const sendMove = ({ gameId, playerId, x, y }) => {
  if (stompClient && stompClient.connected) {
    console.log("📤 Sending move:", { gameId, playerId, x, y });
    stompClient.publish({
      destination: `/app/move`,
      body: JSON.stringify({ gameId, playerId, x, y }),
    });
  } else {
    console.error("❌ WebSocket not connected!");
  }
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
    currentGameId = null;
  }
};
