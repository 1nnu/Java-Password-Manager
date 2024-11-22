import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../hooks/useAuth';

export const Vault = () => {
  const { logout, login, user } = useAuth();
  const [isMasterSet, setIsMasterSet] = useState(null);
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [vaultPassword, setVaultPassword] = useState('');
  const [newIdentifier, setNewIdentifier] = useState('');
  const [identifiers, setIdentifiers] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isVaultAccessed, setIsVaultAccessed] = useState(false);

  const handleLogout = () => {
    logout();
  };

  useEffect(() => {
    const checkMasterPassword = async () => {
      try {
        const response = await axios.get("https://localhost:8080/api/ismasterset", {
          headers: {
            Authorization: `Bearer ${user}`
          }
        });
        setIsMasterSet(response.data["isSet"]);
      } catch (err) {
        setError("Error checking master password status.");
      }
    };
    checkMasterPassword();
  }, [user]);

  const handleSetMasterPassword = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      const response = await axios.post("https://localhost:8080/api/setmaster", { password }, {
        headers: {
          Authorization: `Bearer ${user}`
        }
      });
      if (response.status === 201) {
        setIsMasterSet(true);
        setSuccess("Master password set successfully.");
      }
    } catch (err) {
      setError("Failed to set master password.");
    }
  };

  const handleEnterVault = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await axios.post("https://localhost:8080/api/insertmaster", { password: vaultPassword }, {
        headers: {
          Authorization: `Bearer ${user}`
        }
      });
      if (response.status === 200) {
        const { jwtToken } = response.data;
        login(jwtToken);
        setSuccess("Access granted to vault.");
        setIsVaultAccessed(true);
        fetchIdentifiers(); // Fetch identifiers after accessing the vault
      }
    } catch (err) {
      setError("Invalid master password.");
    }
  };

  const handleGetPassword = async (identifier) => {
    try {
      const response = await axios.get(`https://localhost:8080/api/keys/${identifier}`, {
        headers: {
          Authorization: `Bearer ${user}`
        }
      });
      alert(`Password for ${identifier}: ${response.data.password}`);
    } catch (err) {
      alert("Failed to fetch the password.");
    }
  };

  const fetchIdentifiers = async () => {
    try {
      const response = await axios.get("https://localhost:8080/api/keys", {
        headers: {
          Authorization: `Bearer ${user}`
        }
      });
      setIdentifiers(response.data.identifiers);
    } catch (err) {
      setError("Failed to fetch identifiers.");
    }
  };

  const handleAddIdentifier = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!/^[0-9a-z_]+$/i.test(newIdentifier) || newIdentifier.length < 3 || newIdentifier.length > 255) {
      setError("Identifier must be between 3 and 255 characters, containing only letters, numbers, and underscores.");
      return;
    }

    try {
      const response = await axios.post("https://localhost:8080/api/keys", { identifier: newIdentifier }, {
        headers: {
          Authorization: `Bearer ${user}`
        }
      });
      if (response.status === 201) {
        setSuccess("New derived key created successfully.");
        setNewIdentifier(''); // Clear input
        fetchIdentifiers(); // Refresh the list of identifiers
      }
    } catch (err) {
      if (err.response && err.response.status === 400) {
        setError("Failed to create a new derived key. Check your input.");
      } else {
        setError("An unexpected error occurred.");
      }
    }
  };

  const handleDeleteIdentifier = async (identifier) => {
    if (window.confirm(`Are you sure you want to delete the identifier "${identifier}"?`)) {
      try {
        await axios.delete(`https://localhost:8080/api/keys/${identifier}`, {
          headers: {
            Authorization: `Bearer ${user}`
          }
        });
        setSuccess(`Identifier ${identifier} deleted successfully.`);
        fetchIdentifiers(); // Refresh the list of identifiers
      } catch (err) {
        setError("Failed to delete the identifier.");
      }
    }
  };

  return (
    <div>
      <h1>This is a vault login page</h1>
      <button onClick={handleLogout}>Logout</button>
      <p>Hello, {user ? user.name : "Guest"}</p>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {success && <p style={{ color: 'green' }}>{success}</p>}

      {isMasterSet === null ? (
        <p>Loading...</p>
      ) : isMasterSet === false ? (
        // Prompt for setting master password
        <form onSubmit={handleSetMasterPassword}>
          <h3>Set Master Password</h3>
          <label>
            Master Password:
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>
          <br />
          <label>
            Confirm Password:
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit">Set Master Password</button>
        </form>
      ) : isVaultAccessed === false ? (
        // Prompt to enter the vault password
        <form onSubmit={handleEnterVault}>
          <h3>Enter Vault Password</h3>
          <label>
            Vault Password:
            <input
              type="password"
              value={vaultPassword}
              onChange={(e) => setVaultPassword(e.target.value)}
              required
            />
          </label>
          <br />
          <button type="submit">Enter Vault</button>
        </form>
      ) : (
        <div>
          <h3>Identifiers</h3>
          {identifiers.length > 0 ? (
            <ul>
              {identifiers.map((identifier, index) => (
                <li key={index}>
                  {identifier}
                  <button onClick={() => handleGetPassword(identifier)}>Get Password</button>
                  <button onClick={() => handleDeleteIdentifier(identifier)}>Delete</button>
                </li>
              ))}
            </ul>
          ) : (
            <p>No identifiers found.</p>
          )}
          <form onSubmit={handleAddIdentifier}>
            <h3>Create New Derived Key</h3>
            <label>
              Identifier:
              <input
                type="text"
                value={newIdentifier}
                onChange={(e) => setNewIdentifier(e.target.value)}
                required
              />
            </label>
            <br />
            <button type="submit">Add Identifier</button>
          </form>
        </div>
      )}
    </div>
  );
};
