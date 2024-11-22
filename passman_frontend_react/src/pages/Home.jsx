import { Link } from "react-router-dom";
import React from 'react';

export const HomePage = () => (
    <div>
      <h1>Welcome to passman!</h1>
      <Link to="/login">Log in</Link>
      <br></br>
      <Link to="/register">Register</Link>
      <br></br>
      <Link to="/vault">Vault</Link>
    </div>
  );