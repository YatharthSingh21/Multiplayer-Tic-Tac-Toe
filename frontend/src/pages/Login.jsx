import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import "./Login.css";

function Login() {
  const [mode, setMode] = useState("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const storedUser = localStorage.getItem("username");
    if (storedUser) {
      navigate("/home");
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg("");

    if (!username || !password) {
      setErrorMsg("Please enter both username and password");
      return;
    }

    const url =
      mode === "login"
        ? "http://localhost:8080/api/login"
        : "http://localhost:8080/api/register";

    try {
      const res = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(
          text || `${mode === "login" ? "Login" : "Registration"} failed`
        );
      }

      let data = null;
      try {
        data = await res.json();
      } catch {
        data = { username };
      }

      localStorage.setItem("username", username);
      navigate("/home");
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || "Something went wrong");
    }
  };

  return (
    <div className="login-container">
      <h1> 
        <span className="xo-decoration">⭕</span>
        {mode === "login" ? "Login" : "Register"}
        <span className="xo-decoration">❌</span>
      </h1>

      <form className="login-form" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Enter Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          type="password"
          placeholder="Enter Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit">
          {mode === "login" ? "Login" : "Register"}
        </button>
      </form>

      {errorMsg && <p className="error-text">{errorMsg}</p>}

      <p className="signup-text">
        {mode === "login" ? (
          <>
            Don’t have an account?{" "}
            <button
              type="button"
              className="switch-btn"
              onClick={() => setMode("register")}
            >
              Register
            </button>
          </>
        ) : (
          <>
            Already have an account?{" "}
            <button
              type="button"
              className="switch-btn"
              onClick={() => setMode("login")}
            >
              Login
            </button>
          </>
        )}
      </p>
    </div>
  );
}

export default Login;
