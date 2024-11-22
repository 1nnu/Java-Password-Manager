import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const RequireAuth = ({ children }) => {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    // Redirect unauthenticated users to the login page
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};
