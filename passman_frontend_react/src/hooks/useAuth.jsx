import { createContext, useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import React from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(localStorage.getItem("jwtToken") || null);

  const login = (token) => {
    localStorage.removeItem("jwtToken"); // Clear any existing token first
    localStorage.setItem("jwtToken", token);
    setUser(token);
  };
  

  const logout = () => {
    setUser(null);
    localStorage.removeItem("jwtToken");
    navigate("/", { replace: true });
  };

  const value = { user, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};
