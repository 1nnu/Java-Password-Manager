import React, { useState } from 'react';
import { Link } from "react-router-dom";
import axios from 'axios';

// Axios utility for registration request
export const axiosUtil = {
  register: (registrationData) => {
    return axios.post("https://localhost:8080/api/register", registrationData);
  }
};

export const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [recoveryKey, setRecoveryKey] = useState('');

  // Regex patterns
  const usernamePattern = /^[0-9a-z_]+$/i;
  const passwordPattern = /^[0-9a-z_!@#$%^()+\-={}[\]|\\:;<>.,?/~]+$/i;

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!username || !usernamePattern.test(username) || username.length < 3 || username.length > 35) {
      setError("Username must be between 3 and 35 characters, containing only letters, numbers, and underscores.");
      return;
    }
    if (!password || !passwordPattern.test(password) || password.length < 14 || password.length > 255) {
      setError("Password must be at least 14 characters, with no invalid characters.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setError(''); // Clear error
    setSuccess(''); // Clear success message
    setRecoveryKey(''); // Clear previous recovery key

    try {
      const response = await axiosUtil.register({ username, password });
      if (response.status === 200) {
        const { recoveryKey } = response.data;
        setSuccess("Registration successful!");
        setRecoveryKey(recoveryKey); // Store the recovery key
      }
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setError(err.response.data["error"]);
      } else {
        setError("Registration failed. Please try again.");
      }
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <form onSubmit={handleRegister}>
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
        <div>
          <label>Confirm Password:</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}
        {recoveryKey && (
          <div>
            <p style={{ fontSize: '20px', fontWeight: 'bold', color: 'blue' }}>
              Your Recovery Key: {recoveryKey}
            </p>
            <p style={{ color: 'red' }}>
              Please save this recovery key securely. You will need it to recover your account.
            </p>
          </div>
        )}
        <button type="submit">Register</button>
      </form>
      <br />
      <Link to="/login">Go to login</Link>
    </div>
  );
};
