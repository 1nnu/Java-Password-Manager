import React, { useState } from 'react';
import axios from 'axios';
import { Link } from "react-router-dom";

// Axios utility for recovery request
export const axiosUtil = {
  recover: (recoveryData) => {
    return axios.post("https://localhost:8080/api/recovery", recoveryData);
  }
};

export const Recovery = () => {
  const [recoveryKey, setRecoveryKey] = useState('');
  const [username, setUsername] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [newRecoveryKey, setNewRecoveryKey] = useState('');

  // Regex patterns
  const recoveryKeyPattern = /^[0-9a-z]+$/i;
  const usernamePattern = /^[0-9a-z_]+$/i;
  const passwordPattern = /^[0-9a-z_!@#$%^()+\-={}[\]|\\:;<>.,?/~]+$/i;

  const handleRecovery = async (e) => {
    e.preventDefault();

    if (!recoveryKey || !recoveryKeyPattern.test(recoveryKey) || recoveryKey.length > 50) {
      setError("Invalid recovery key.");
      return;
    }
    if (!username || !usernamePattern.test(username)) {
      setError("Invalid username.");
      return;
    }
    if (!newPassword || !passwordPattern.test(newPassword) || newPassword.length > 255) {
      setError("Password must be at most 255 characters and contain only valid characters.");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setError(''); // Clear error
    setSuccess(''); // Clear success message
    setNewRecoveryKey(''); // Clear previous recovery key

    try {
      const response = await axiosUtil.recover({ recoveryKey, username, newPassword });
      if (response.status === 200) {
        const { recoveryKey: newKey } = response.data;
        setSuccess("Password has been updated successfully!");
        setNewRecoveryKey(newKey); // Display the new recovery key
      }
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setError(err.response.data["error"]);
      } else {
        setError("Recovery failed. Please try again.");
      }
    }
  };

  return (
    <div>
      <h2>Account Recovery</h2>
      <form onSubmit={handleRecovery}>
        <div>
          <label>Recovery Key:</label>
          <input
            type="text"
            value={recoveryKey}
            onChange={(e) => setRecoveryKey(e.target.value)}
            required
          />
        </div>
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
          <label>New Password:</label>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Confirm New Password:</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {success && <p style={{ color: 'green' }}>{success}</p>}
        {newRecoveryKey && (
          <div>
            <p style={{ fontSize: '20px', fontWeight: 'bold', color: 'blue' }}>
              Your New Recovery Key: {newRecoveryKey}
            </p>
            <p style={{ color: 'red' }}>
              Please save this new recovery key securely. You will need it for future account recoveries.
            </p>
          </div>
        )}
        <button type="submit">Recover Account</button>
      </form>
      <br />
      <Link to="/login">Back to login</Link>
    </div>
  );
};
