# Multiplayer Tic-Tac-Toe — Real-Time Web Game

A **real-time, server-authoritative multiplayer Tic-Tac-Toe** game built with **Spring Boot (WebSocket backend)** and **React (frontend)**.  
Players can join lobbies, play against each other live, and see instant board updates — all deployed and production-ready.  

---

##  Features

- ✅ **Real-time gameplay** powered by WebSockets  
- ✅ **Server-authoritative architecture** — prevents cheating and ensures sync consistency  
- ✅ **Interactive lobby system** with unique game IDs  
- ✅ **Responsive UI** designed for mobile-first gameplay  
- ✅ **Automatic result detection** (win, loss, draw)  
- ✅ **Leaderboard tracking** for player stats  
- ✅ **Auto-return to homepage** after game ends  
- ✅ **Fully deployed** — works end-to-end!

---
---
## 🚀 Try Yourself

Use these testIDs, to actually test the game
Username - test3000, password - maman
Username - test4000, password - maman
Username - test3899, password - maman
---

---
## Tech Stack

### **Frontend**
- ⚛️ React (Vite)
- 🎨 CSS3 with responsive design
- 🔁 WebSocket client for live updates

### **Backend**
- ☕ Spring Boot (Java)
- 🔌 STOMP over WebSocket for real-time messaging
- 🧮 Game state management on the server
- 🗃️ In-memory storage for active games and players

### **Deployment**
- 🌐 Frontend: Render / Netlify  
- 🔧 Backend: Render / Railway  
- 🔒 Proper CORS & WebSocket production configuration  

---

---

## Architecture Overview

```plaintext
+-----------------------+
|      React (UI)       |
|  - Lobby / GameBoard  |
|  - WebSocket Client   |
+----------↑------------+
           |
           | STOMP Messages (move/join/updates)
           ↓
+-----------------------+
|   Spring Boot Server  |
|  - GameSocketController|
|  - GameService         |
|  - Server Game State   |
+-----------------------+
