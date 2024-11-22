import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from "react-router-dom";
import axios from 'axios';
import { useAuth } from '../hooks/useAuth';

export const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Check if user is already logged in
  useEffect(() => {
    if (localStorage.getItem("jwtToken")) {
      navigate("/vault", { replace: true });
    }
  }, [navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("https://localhost:8080/api/login", {
        username,
        password
      });
      if (response.status === 200) {
        const { jwtToken } = response.data;
        setSuccess("Logged in, can enter vault!");
        login(jwtToken);
        navigate("/vault", { replace: true });
      }
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setError(err.response.data["error"]);
      } else {
        setError("Login failed. Please try again.");
      }
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}
        <button type="submit">Login</button>
      </form>
      <br />
      <Link to="/register">Go to register</Link>
      <br />
      <Link to="/vault">Go to vault</Link>
      <br />
      <Link to="/recovery">Recover account?</Link>
    </div>
  );
};
